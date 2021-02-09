package com.b0c0.common;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class InteriorThreadPoolFactory {

    private static final ThreadFactory GENERAL_THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("general-pool-%d").build();

    /**
     * corePoolSize：核心线程池大小
     * maximumPoolSize：最大线程池大小
     * keepAliveTime：线程最大空闲时间
     * unit：时间单位
     * workQueue：线程等待队列  四种队列 1.ArrayBlockingQueue：有界队列，2.SynchronousQueue：同步队列，3.LinkedBlockingQueue：无界队列，4.DelayQueue：延时阻塞队列
     * threadFactory：线程创建工厂
     * handler：拒绝策略 四种策略 1.ThreadPoolExecutor.AbortPolicy()：2.ThreadPoolExecutor.CallerRunsPolicy()：3.ThreadPoolExecutor.DiscardOldestPolicy()：4.ThreadPoolExecutor.DiscardPolicy()
     */
    private static final ExecutorService GENERAL = new ThreadPoolExecutor(4, 10,
            30L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024), GENERAL_THREAD_FACTORY, new ThreadPoolExecutor.CallerRunsPolicy());


    public static ExecutorService getGeneral() {
        return GENERAL;
    }

}
