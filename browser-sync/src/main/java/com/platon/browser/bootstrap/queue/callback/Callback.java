package com.platon.browser.bootstrap.queue.callback;

/**
 * 自检事件回调
 */
public interface Callback {
    void call(long handledBlockNum);
}
