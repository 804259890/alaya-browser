package com.platon.browser.service.govern;

import com.platon.browser.client.PlatOnClient;
import com.platon.browser.config.BlockChainConfig;
import com.platon.browser.config.govern.ModifiableParam;
import com.platon.browser.dao.entity.Config;
import com.platon.browser.dao.mapper.ConfigMapper;
import com.platon.browser.dao.mapper.CustomConfigMapper;
import com.platon.browser.enums.ModifiableGovernParamEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.platon.bean.GovernParam;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 治理参数服务
 * @author: chendongming@juzix.net
 * @create: 2019-11-25 20:36:04
 **/
@Slf4j
@Service
@Transactional
public class ParameterService {

    @Autowired
    private ConfigMapper configMapper;
    @Autowired
    private PlatOnClient platOnClient;
    @Autowired
    private BlockChainConfig chainConfig;
    @Autowired
    private CustomConfigMapper customConfigMapper;

    /**
     * 使用debug_economic_config接口返回的数据初始化配置表，只有从第一个块开始同步时需要调用
     */
    public void initConfigTable() throws Exception {
        configMapper.deleteByExample(null);
        List<GovernParam> governParamList = platOnClient.getProposalContract().getParamList("").send().data;
        List<Config> configList = new ArrayList<>();
        int id = 1;
        for (GovernParam gp : governParamList) {
            Config config = new Config();
            config.setId(id);
            config.setModule(gp.getParamItem().getModule());
            config.setName(gp.getParamItem().getName());
            config.setRangeDesc(gp.getParamItem().getDesc());
            config.setActiveBlock(0L);
            config.setValue(config.getActiveBlock()==Integer.parseInt(gp.getParamValue().getActiveBlock())?gp.getParamValue().getStaleValue():gp.getParamValue().getValue());
            configList.add(config);

            // 更新内存中的blockChainConfig中在init_value,stale_value,value字段值
            String initValue = getValueInBlockChainConfig(config.getName());
            config.setInitValue(initValue);
            config.setStaleValue(config.getInitValue());

            if(StringUtils.isBlank(config.getValue())) config.setValue(config.getInitValue());
            id++;
        }
        configMapper.batchInsert(configList);
    }

    /**
     * 使用配置表中的配置覆盖内存中的BlockChainConfig，在重新启动的时候调用
     */
    public void overrideBlockChainConfig(){
        // 使用数据库config表的配置覆盖当前配置
        List<Config> configList = configMapper.selectByExample(null);
        ModifiableParam modifiableParam = ModifiableParam.builder().build().init(configList);

        //创建验证人最低的质押Token数(K)
        chainConfig.setStakeThreshold(modifiableParam.getStaking().getStakeThreshold());
        //委托人每次委托及赎回的最低Token数(H)
        chainConfig.setDelegateThreshold(modifiableParam.getStaking().getOperatingThreshold());
        //节点质押退回锁定周期
        chainConfig.setUnStakeRefundSettlePeriodCount(modifiableParam.getStaking().getUnStakeFreezeDuration().toBigInteger());
        //备选验证节点数量(U)
        chainConfig.setConsensusValidatorCount(modifiableParam.getStaking().getMaxValidators().toBigInteger());
        //举报最高处罚n3‱
        chainConfig.setDuplicateSignSlashRate(modifiableParam.getSlashing().getSlashFractionDuplicateSign().divide(BigDecimal.valueOf(10000),16, RoundingMode.FLOOR));
        //举报奖励n4%
        chainConfig.setDuplicateSignRewardRate(modifiableParam.getSlashing().getDuplicateSignReportReward().divide(BigDecimal.valueOf(100),2,RoundingMode.FLOOR));
        //证据有效期
        chainConfig.setEvidenceValidEpoch(modifiableParam.getSlashing().getMaxEvidenceAge());
        //扣除区块奖励的个数
        chainConfig.setSlashBlockRewardCount(modifiableParam.getSlashing().getSlashBlocksReward());
        //默认每个区块的最大Gas
        chainConfig.setMaxBlockGasLimit(modifiableParam.getBlock().getMaxBlockGasLimit());
    }

    /**
     * 配置值轮换：value旧值覆盖到stale_value，参数中的新值覆盖value
     * @param activeConfigList 被激活的配置信息列表
     */
    @Transactional
    public void rotateConfig(List<Config> activeConfigList) {
        // 更新配置表
        customConfigMapper.rotateConfig(activeConfigList);
        //更新内存中的BlockChainConfig
        overrideBlockChainConfig();
    }

    /**
     * 根据参数提案中的参数name获取当前blockChainConfig中的对应的当前值
     * @param name
     * @return
     */
    public String getValueInBlockChainConfig(String name) {
        ModifiableGovernParamEnum paramEnum = ModifiableGovernParamEnum.getMap().get(name);
        String staleValue = "";
        switch (paramEnum){
            // 质押相关
            case STAKE_THRESHOLD:
                staleValue = Convert.toVon(chainConfig.getStakeThreshold(), Convert.Unit.LAT).toString();
                break;
            case OPERATING_THRESHOLD:
                staleValue = Convert.toVon(chainConfig.getDelegateThreshold(), Convert.Unit.LAT).toString();
                break;
            case MAX_VALIDATORS:
                staleValue = chainConfig.getConsensusValidatorCount().toString();
                break;
            case UN_STAKE_FREEZE_DURATION:
                staleValue = chainConfig.getUnStakeRefundSettlePeriodCount().toString();
                break;
            // 惩罚相关
            case SLASH_FRACTION_DUPLICATE_SIGN:
                staleValue = chainConfig.getDuplicateSignSlashRate().multiply(BigDecimal.valueOf(10000)).toString();
                break;
            case DUPLICATE_SIGN_REPORT_REWARD:
                staleValue = chainConfig.getDuplicateSignRewardRate().multiply(BigDecimal.valueOf(100)).toString();
                break;
            case MAX_EVIDENCE_AGE:
                staleValue = chainConfig.getEvidenceValidEpoch().toString();
                break;
            case SLASH_BLOCKS_REWARD:
                staleValue = chainConfig.getSlashBlockRewardCount().toString();
                break;
            // 区块相关
            case MAX_BLOCK_GAS_LIMIT:
                staleValue = chainConfig.getMaxBlockGasLimit().toString();
                break;
            default:
                break;
        }
        return staleValue;
    }
}
