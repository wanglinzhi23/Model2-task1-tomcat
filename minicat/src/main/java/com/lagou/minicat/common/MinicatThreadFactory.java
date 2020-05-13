package com.lagou.minicat.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * minicat线程工厂
 *
 * @author wlz
 * @date 2020/5/12
 */
public class MinicatThreadFactory implements ThreadFactory {

    private AtomicInteger threadCounter;

    public MinicatThreadFactory() {
        threadCounter = new AtomicInteger(0);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("minicat-thread-exec-" + threadCounter.getAndIncrement());
        return thread;
    }
}
