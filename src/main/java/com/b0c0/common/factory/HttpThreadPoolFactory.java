package com.b0c0.common.factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class HttpThreadPoolFactory {

    private static final ThreadFactory GENERAL_THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("b0c0-common-http-pool-%d").build();

    //核心线程池大小
    private static final int CORE_POOL_SIZE = 9;
    //最大线程池大小
    private static final int MAX_POOL_SIZE = 19;
    //线程任务队列大小
    private static final int QUEUE_CAPACITY = 1;
    //空闲线程的存活时间.默认情况下核心线程不会退出
    private static final int KEEP_ALIVE_TIME = 15;

    /**
     * corePoolSize：核心线程池大小
     * maximumPoolSize：最大线程池大小
     * keepAliveTime：线程最大空闲时间
     * unit：时间单位
     * workQueue：线程等待队列  四种队列 1.ArrayBlockingQueue：有界队列，2.SynchronousQueue：同步队列，3.LinkedBlockingQueue：无界队列，4.DelayQueue：延时阻塞队列
     * threadFactory：线程创建工厂
     * handler：拒绝策略 四种策略 1.ThreadPoolExecutor.AbortPolicy()：2.ThreadPoolExecutor.CallerRunsPolicy()：3.ThreadPoolExecutor.DiscardOldestPolicy()：4.ThreadPoolExecutor.DiscardPolicy()
     */
    private static final ExecutorService thread = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
            KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(QUEUE_CAPACITY), GENERAL_THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());

    public static ExecutorService getThread() {
        return thread;
    }

}
