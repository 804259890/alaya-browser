package com.platon.browser.common.service.elasticsearch;

import com.platon.browser.elasticsearch.TokenTransferRecordESRepository;
import com.platon.browser.elasticsearch.dto.ESTokenTransferRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doThrow;

/**
 * @Author: AgentRJ
 * @Date: 2020/9/24
 * @Version 1.0
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class EsTokenTransferRecordServiceTest {

    @Mock
    private TokenTransferRecordESRepository tokenTransferRecordESRepository;

    @Spy
    private EsTokenTransferRecordService target;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(this.target, "tokenTransferRecordESRepository", this.tokenTransferRecordESRepository);
    }

    @Test(expected = Exception.class)
    public void save() throws IOException {
        this.target.save(Collections.emptySet());
        Set<ESTokenTransferRecord> data = new HashSet<>();
        ESTokenTransferRecord esTokenTransferRecord = new ESTokenTransferRecord();
        esTokenTransferRecord.setContract("123");
        esTokenTransferRecord.setHash("123");
        esTokenTransferRecord.setFrom("123");
        esTokenTransferRecord.setTto("123");
        esTokenTransferRecord.setSeq(1l);
        data.add(esTokenTransferRecord);
        this.target.save(data);

        doThrow(new RuntimeException("")).when(this.tokenTransferRecordESRepository).bulkAddOrUpdate(anyMap());
        this.target.save(data);
        /*Set<ESTokenTransferRecord> records = new HashSet<>();
        records.add(ESTokenTransferRecord.builder().seq(1l).hash("11txhash-01").bn(1l).bTime(new Date())
                .contract("0xfe00596d06310daf0f2afff1072c8adc57158e84").decimal(6).sign("methodSign").from("0x38b98a6358d9247a96bc39ee4a0c1020f5551151").result(1).build());
        records.add(ESTokenTransferRecord.builder().seq(2l).hash("112txhash-01").bn(1l).bTime(new Date()).tto("0x38b98a6358d9247a96bc39ee4a0c1020f5551151")
                .contract("0x855b837dcbcd18a7f71d58a3d0b768e9eef2acc6").decimal(6).sign("methodSign").from("0x855b837dcbcd18a7f71d58a3d0b768e9eef2acc6").result(1).build());
        records.add(ESTokenTransferRecord.builder().seq(3l).hash("113txhash-01").bn(1l).bTime(new Date()).tto("0x38b98a6358d9247a96bc39ee4a0c1020f5551151")
                .contract("0x38b98a6358d9247a96bc39ee4a0c1020f5551156").decimal(6).sign("methodSign").from("0x38b98a6358d9247a96bc39ee4a0c1020f5551159").result(1).build());
        esTokenTransferRecordService.save(records);
        System.out.println(".");*/
    }
}