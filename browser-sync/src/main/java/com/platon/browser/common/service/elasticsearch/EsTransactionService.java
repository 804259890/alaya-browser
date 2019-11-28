package com.platon.browser.common.service.elasticsearch;

import com.platon.browser.elasticsearch.TransactionESRepository;
import com.platon.browser.elasticsearch.dto.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Auther: Chendongming
 * @Date: 2019/10/25 15:12
 * @Description: ES服务
 */
@Slf4j
@Service
public class EsTransactionService implements EsService<Transaction>{
    @Autowired
    private TransactionESRepository transactionESRepository;
    @Retryable(value = Exception.class, maxAttempts = Integer.MAX_VALUE)
    public void save(Set<Transaction> transactions) throws IOException {
        if(transactions.isEmpty()) return;
        try {
            Map<String,Transaction> transactionMap = new HashMap<>();
            // 使用交易Hash作ES的docId
            transactions.forEach(t->transactionMap.put(t.getHash(),t));
            transactionESRepository.bulkAddOrUpdate(transactionMap);
        }catch (Exception e){
            log.error("",e);
            throw e;
        }
    }
}
