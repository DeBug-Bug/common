package com.b0c0.common.factory;



import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * 统一创建线程池，管理
 */
public class ThreadPoolFactory {

    private static final ThreadFactory GENERAL_THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("com.b0c0.commom.delayedQueue-pool-general-%d").build();
    //核心线程池大小
    private static final int CORE_POOL_SIZE = 5;
    //最大线程池大小
    private static final int MAX_POOL_SIZE = 20;
    //线程任务队列大小
    private static final int QUEUE_CAPACITY = 1024;
    //空闲线程的存活时间.默认情况下核心线程不会退出
    private static final int KEEP_ALIVE_TIME = 20;

    /**
     * unit：时间单位
     * workQueue：线程等待队列  四种队列 1.ArrayBlockingQueue：有界队列，2.SynchronousQueue：同步队列，3.LinkedBlockingQueue：无界队列，4.DelayQueue：延时阻塞队列
     * threadFactory：线程创建工厂
     * handler：拒绝策略 四种策略 1.ThreadPoolExecutor.AbortPolicy()：2.ThreadPoolExecutor.CallerRunsPolicy()：3.ThreadPoolExecutor.DiscardOldestPolicy()：4.ThreadPoolExecutor.DiscardPolicy()
     */
    private static final ExecutorService DELAYED_QUEUE_THREAD_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(QUEUE_CAPACITY), GENERAL_THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());

    public static ExecutorService getDelayedQueueThreadExecutor() {
        return DELAYED_QUEUE_THREAD_EXECUTOR;
    }


}
