package com.platon.browser.req.newtransaction;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 交易详情请求对象
 *  @file TransactionDetailsReq.java
 *  @description 
 *	@author zhangrj
 *  @data 2019年8月31日
 */
@Data
public class TransactionDetailsReq{
    @NotBlank(message = "{txHash not null}")
    private String txHash;
}