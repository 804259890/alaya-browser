package com.platon.browser.param;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * User: dongqile
 * Date: 2019/8/6
 * Time: 14:51
 * txType=1001修改质押信息(编辑验证人)
 */
@Data
@Builder
@Accessors(chain = true)
public class EditValidatorParam extends TxParam{

    /**
     * 用于接受出块奖励和质押奖励的收益账户
     */
    private String benefitAddress;

    /**
     * 被质押的节点Id(也叫候选人的节点Id)
     */
    private String nodeId;

    /**
     * 外部Id(有长度限制，给第三方拉取节点描述的Id)
     */
    private String externalId;

    /**
     * 被质押节点的名称(有长度限制，表示该节点的名称)
     */
    private String nodeName;

    /**
     * 节点的第三方主页(有长度限制，表示该节点的主页)
     */
    private String website;

    /**
     * 节点的描述(有长度限制，表示该节点的描述)
     */
    private String details;

    /**
     * blockNumber
     */
    private String blockNumber;

    private String perNodeName;
}
