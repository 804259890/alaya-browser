package com.platon.browser.common.complement.cache;

import com.platon.browser.AgentTestBase;
import com.platon.browser.common.complement.cache.bean.NodeItem;
import com.platon.browser.exception.NoSuchBeanException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.Silent.class)
public class NodeCacheTest extends AgentTestBase {

    @Spy
    private NodeCache nodeCache;
    @Test
    public void test() throws NoSuchBeanException {
        nodeCache.init(new ArrayList<>(nodeList));
        NodeItem nodeItem = NodeItem.builder()
                .stakingBlockNum(BigInteger.ONE)
                .nodeName("3r3")
                .nodeId("0xsfsf233r")
                .build();
        nodeCache.addNode(nodeItem);
        nodeCache.getNode("0xsfsf233r");
    }
}