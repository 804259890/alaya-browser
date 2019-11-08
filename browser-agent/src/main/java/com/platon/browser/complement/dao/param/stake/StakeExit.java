package com.platon.browser.complement.dao.param.stake;

import java.math.BigInteger;
import java.util.Date;

import com.platon.browser.complement.dao.param.BusinessParam;
import com.platon.browser.common.enums.BusinessType;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 退出质押 入库参数
 * @author: chendongming@juzix.net
 * @create: 2019-11-04 17:58:27
 **/
@Data
@Slf4j
@Builder
@Accessors(chain = true)
public class StakeExit extends BusinessParam {
    /**
     * 节点id
     */
    private String nodeId;

    /**
     * 质押交易所在块高
     */
    private BigInteger stakingBlockNum;

    /**
     * 时间
     */
    private Date time;

    /**
     * 结算周期标识
     */
    private int stakingReductionEpoch;

    @Override
    public BusinessType getBusinessType() {
        return BusinessType.STAKE_EXIT;
    }
}