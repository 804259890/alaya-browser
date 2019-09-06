package com.platon.browser.task;

import com.platon.browser.bean.CollectResult;
import com.platon.browser.client.PlatonClient;
import com.platon.browser.client.SpecialContractApi;
import com.platon.browser.config.BlockChainConfig;
import com.platon.browser.dao.mapper.CustomBlockMapper;
import com.platon.browser.dto.CustomBlock;
import com.platon.browser.dto.CustomNode;
import com.platon.browser.dto.CustomStaking;
import com.platon.browser.engine.BlockChain;
import com.platon.browser.engine.cache.AddressCacheUpdater;
import com.platon.browser.engine.cache.StakingCacheUpdater;
import com.platon.browser.engine.stage.BlockChainStage;
import com.platon.browser.exception.BlockCollectingException;
import com.platon.browser.exception.BusinessException;
import com.platon.browser.exception.CacheConstructException;
import com.platon.browser.exception.CandidateException;
import com.platon.browser.service.BlockService;
import com.platon.browser.service.CandidateService;
import com.platon.browser.service.DbService;
import com.platon.browser.service.TransactionService;
import com.platon.browser.utils.HexTool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.platon.BaseResponse;
import org.web3j.platon.bean.Node;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.platon.browser.engine.BlockChain.NODE_NAME_MAP;

/**
 * @Auther: Chendongming
 * @Date: 2019/8/10 09:14
 * @Description: 区块和交易同步任务
 */
@Component
public class BlockSyncTask {
    private static Logger logger = LoggerFactory.getLogger(BlockSyncTask.class);
    public static ExecutorService THREAD_POOL;

    @Autowired
    private CustomBlockMapper customBlockMapper;
    @Autowired
    private DbService dbService;
    @Autowired
    private BlockChain blockChain;

    @Autowired
    private BlockChainConfig chainConfig;
    @Autowired
    private PlatonClient client;
    @Autowired
    private AddressCacheUpdater addressCacheUpdater;
    @Autowired
    private StakingCacheUpdater stakingCacheUpdater;
    @Autowired
    private BlockService blockService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private CandidateService candidateService;

    // 已采集入库的最高块
    private long commitBlockNumber = 0;

    // 每一批次采集区块的数量
    @Value("${platon.web3j.collect.batch-size}")
    private int collectBatchSize;

    /**
     * 初始化已有业务数据
     */
    public void init () throws Exception {
        THREAD_POOL = Executors.newFixedThreadPool(collectBatchSize);
        // 从数据库查询最高块号，赋值给commitBlockNumber
        Long maxBlockNumber = customBlockMapper.selectMaxBlockNumber();
        // 更新当前所在周期的区块奖励和结算周期质押奖励, 初始化共识验证人列表
        Long initBlockNumber = 1L;
        if (maxBlockNumber != null && maxBlockNumber > 0) {
            commitBlockNumber = maxBlockNumber;
            blockChain.updateReward(maxBlockNumber);
            initBlockNumber = maxBlockNumber;
        }else{
            blockChain.updateReward(0L);
        }
        candidateService.initValidator(initBlockNumber);
        candidateService.initVerifier(initBlockNumber);

        /*
         * 从第一块同步的时候，结算周期验证人和共识周期验证人是链上内置的
         * 查询内置共识周期验证人初始化blockChain的curValidator属性
         * 查询内置结算周期验证人初始化blockChain的curVerifier属性
          */
        if(maxBlockNumber==null){
            // 如果库里区块为空，则：
            try {
                // 根据区块号0查询共识周期验证人，以便对结算周期验证人设置共识标识
                BaseResponse<List<Node>> result = SpecialContractApi.getHistoryValidatorList(client.getWeb3j(),BigInteger.ZERO);
                if(!result.isStatusOk()){
                    logger.debug("查询实时共识周期验证人列表...");
                    result = client.getNodeContract().getValidatorList().send();
                    if(!result.isStatusOk()){
                        throw new CandidateException("底层链查询实时共识周期验证节点列表出错:"+result.errMsg);
                    }
                }
                // 查询内置共识周期验证人初始化blockChain的curValidator属性
                Set<String> validatorSet = new HashSet<>();
                result.data.forEach(node->validatorSet.add(HexTool.prefix(node.getNodeId())));

                // 查询所有候选人
                Map<String,Node> candidateMap = new HashMap<>();
                result = client.getNodeContract().getCandidateList().send();
                if(!result.isStatusOk()){
                    throw new CandidateException("底层链查询候选验证节点列表出错:"+result.errMsg);
                }
                result.data.forEach(node->candidateMap.put(HexTool.prefix(node.getNodeId()),node));

                // 配置中的默认内置节点信息
                Map<String,CustomStaking> defaultStakingMap = new HashMap<>();
                chainConfig.getDefaultStakings().forEach(staking -> defaultStakingMap.put(staking.getNodeId(),staking));

                // 根据区块号0查询结算周期验证人列表并入库
                result = SpecialContractApi.getHistoryVerifierList(client.getWeb3j(),BigInteger.ZERO);
                if(!result.isStatusOk()){
                    logger.debug("查询实时结算周期验证人列表...");
                    result = client.getNodeContract().getVerifierList().send();
                    if(!result.isStatusOk()){
                        throw new CandidateException("底层链查询实时结算周期验证节点列表出错:"+result.errMsg);
                    }
                }

                result.data.stream().filter(Objects::nonNull).forEach(verifier->{
                    Node candidate = candidateMap.get(HexTool.prefix(verifier.getNodeId()));
                    // 补充完整属性
                    if(candidate!=null) BeanUtils.copyProperties(candidate,verifier);

                    CustomNode node = new CustomNode();
                    node.updateWithNode(verifier);
                    node.setIsRecommend(CustomNode.YesNoEnum.YES.code);
                    node.setStatVerifierTime(BigInteger.ONE.intValue()); // 提前设置验证轮数
                    node.setStatExpectBlockQty(chainConfig.getExpectBlockCount().longValue());
                    BlockChain.STAGE_DATA.getStakingStage().insertNode(node);

                    CustomStaking staking = new CustomStaking();
                    staking.updateWithNode(verifier);
                    staking.setStatVerifierTime(BigInteger.ONE.intValue()); // 提前设置验证轮数
                    staking.setIsInit(CustomStaking.YesNoEnum.YES.code);
                    staking.setIsSetting(CustomStaking.YesNoEnum.YES.code);
                    // 内置节点默认设置状态为1
                    staking.setStatus(CustomStaking.StatusEnum.CANDIDATE.code);
                    // 设置内置节点质押锁定金额
                    BigDecimal initStakingLocked = Convert.toVon(chainConfig.getDefaultStakingLockedAmount(), Convert.Unit.LAT);
                    staking.setStakingLocked(initStakingLocked.toString());
                    // 如果当前候选节点在共识周期验证人列表，则标识其为共识周期节点
                    if(validatorSet.contains(node.getNodeId())) staking.setIsConsensus(CustomStaking.YesNoEnum.YES.code);
                    staking.setIsSetting(CustomStaking.YesNoEnum.YES.code);

                    CustomStaking defaultStaking = defaultStakingMap.get(staking.getNodeId());
                    if(StringUtils.isBlank(staking.getStakingName())&&defaultStaking!=null)
                        staking.setStakingName(defaultStaking.getStakingName());

                    // 暂存至新增质押待入库列表
                    BlockChain.STAGE_DATA.getStakingStage().insertStaking(staking);

                    // 更新节点名称映射缓存
                    NODE_NAME_MAP.put(staking.getNodeId(),staking.getStakingName());
                });
                BlockChainStage bcr = blockChain.exportResult();
                batchSave(Collections.emptyList(),bcr);
                blockChain.commitResult();

                // 通知质押引擎重新初始化节点缓存
                blockChain.getStakingExecute().loadNodes();
            } catch (IOException | CacheConstructException | BusinessException e) {
                throw new CandidateException("查询内置初始验证人列表失败："+e.getMessage());
            }
        }
    }

    public void start() throws Exception {
        while (true) {
            // 从(已采最高区块号+1)开始构造连续的指定数量的待采区块号列表
            Set<BigInteger> blockNumbers = new HashSet<>();
            // 当前链上最新区块号
            BigInteger curChainBlockNumber;
            try {
                curChainBlockNumber = client.getWeb3j().platonBlockNumber().send().getBlockNumber();
            } catch (IOException e) {
                throw new BlockCollectingException("取链上最新区块号失败:"+e.getMessage());
            }
            for (long blockNumber=commitBlockNumber+1; blockNumber<=(commitBlockNumber+collectBatchSize);blockNumber++) {
                // 如果块号>当前链上块号,则不再累加
                if(blockNumber>curChainBlockNumber.longValue()) break;
                blockNumbers.add(BigInteger.valueOf(blockNumber));
            }
            if(blockNumbers.size()==0){
                logger.info("当前链最高块({}),等待链出下一个块...",curChainBlockNumber);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new BlockCollectingException("区块采集暂停被中断:"+e.getMessage());
                }
                continue;
            }
            // 并行采块 ξξξξξξξξξξξξξξξξξξξξξξξξξξξ
            // 采集前先重置结果容器
            CollectResult.reset();
            // 开始并行采集
            blockService.collect(blockNumbers);
            List <CustomBlock> blocks = CollectResult.getSortedBlocks();
            // 采集不到区块则暂停1秒, 结束本次循环
            if(blocks.size()==0) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new BlockCollectingException("区块采集暂停被中断:"+e.getMessage());
                }
                continue;
            }
            // 并行分析 ξξξξξξξξξξξξξξξξξξξξξξξξξξξ
            transactionService.analyze(blocks);
            // 调用BlockChain实例, 串行分析每个区块，获取质押、提案相关业务数据
            for (CustomBlock block:blocks) blockChain.execute(block);
            BlockChainStage bizData = blockChain.exportResult();
            try {
                // 入库失败，立即停止，防止采集后续更高的区块号，导致不连续区块号出现
                batchSave(blocks, bizData);
            } catch (BusinessException e) {
                break;
            }
            // 记录已采入库最高区块号
            commitBlockNumber = blocks.get(blocks.size() - 1).getNumber();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new BlockCollectingException("区块采集暂停被中断:"+e.getMessage());
            }
        }
    }

    private void batchSave(List<CustomBlock> basicData, BlockChainStage bizData) throws BusinessException {
        try{
            // 入库前更新统计信息
            addressCacheUpdater.updateAddressStatistics();
            stakingCacheUpdater.updateStakingStatistics();
            // 串行批量入库
            dbService.insertOrUpdate(basicData,bizData);
            blockChain.commitResult();
            // 缓存整理
            BlockChain.NODE_CACHE.sweep();
            BlockChain.PROPOSALS_CACHE.sweep();
        }catch (Exception e){
            throw new BusinessException("数据批量入库出错："+e.getMessage());
        }
    }
}
