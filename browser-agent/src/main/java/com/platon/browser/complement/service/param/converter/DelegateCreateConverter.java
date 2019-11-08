package com.platon.browser.complement.service.param.converter;

import com.platon.browser.complement.dao.param.delegate.DelegateCreate;
import com.platon.browser.common.queue.collection.event.CollectionEvent;
import com.platon.browser.complement.dao.mapper.DelegateBusinessMapper;
import com.platon.browser.elasticsearch.dto.Transaction;
import com.platon.browser.param.DelegateCreateParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @description: 委托业务参数转换器
 * @author: chendongming@juzix.net
 * @create: 2019-11-04 17:58:27
 **/
@Service
public class DelegateCreateConverter extends BusinessParamConverter<DelegateCreate> {
	
    @Autowired
    private DelegateBusinessMapper delegateBusinessMapper;

    @Override
    public DelegateCreate convert(CollectionEvent event, Transaction tx) {
        DelegateCreateParam txParam = tx.getTxParam(DelegateCreateParam.class);

        DelegateCreate businessParam= DelegateCreate.builder()
        		.nodeId(txParam.getNodeId())
        		.amount(new BigDecimal(txParam.getAmount()))
        		.blockNumber(BigInteger.valueOf(tx.getNum()))
        		.txFrom(tx.getFrom())
        		.sequence(BigInteger.valueOf(tx.getSeq()))
        		.stakingBlockNumber(txParam.getStakingBlockNum())
                .build();
        
        delegateBusinessMapper.create(businessParam);
        return businessParam;
    }
}
