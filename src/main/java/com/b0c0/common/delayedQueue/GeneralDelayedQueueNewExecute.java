package com.b0c0.common.delayedQueue;


import com.b0c0.common.delayedQueue.base.RetryTimeTypeable;
import com.b0c0.common.utils.GeneralResult;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


/**
 * @program: springbootdemo
 * @description: 通用延时队列执行器
 * @author: lidongsheng
 * @createData: 2020-09-21 15:50
 * @updateAuthor: lidongsheng
 * @updateData: 2020-09-21 15:50
 * @updateContent:
 * @Version: 1.0.0
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 * ************************************************
 * Copyright @ 李东升 2020. All rights reserved
 * ************************************************
 */

/**
 * 延时队列执行器就是调用的入口类，此类应该时储存一下延时队列实体以及具体的执行方法实体等，以及承担具体执行。
 * 并且能够支持多线程调用，所以此类已经实现了Runnable接口，开发者可以根据需要来多线程调用或者直接同步调用。
 */
public class GeneralDelayedQueueNewExecute {

    //延时队列
    private static Map<String, DelayQueue<GeneralDelayedQueue>> delayQueueMap = new ConcurrentHashMap<>();
    //延时队列主题信息
    private static Map<String, GeneralDelayedQueue> taskMap = new ConcurrentHashMap<>();
    //重试时间的具体实现
    private static Map<String, RetryTimeTypeable> retryTimeTypeableMap = new ConcurrentHashMap<>();
    //用来保证程序执行完成之后才能获取到执行结果
    private static Map<String, CountDownLatch> countDownLatchMap = new ConcurrentHashMap<>();
    //存储每次执行的具体结果信息
    private static Map<String, List> resultListMap = new ConcurrentHashMap<>();
    //异步执行的线程池
    private static Executor executor;

    static {
        final ThreadFactory GENERAL_THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("com.b0c0.commom.delayedQueue-pool-general-%d").build();
        //核心线程池大小
        final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2 + 1;
        //最大线程池大小
        final int MAX_POOL_SIZE = CORE_POOL_SIZE * 2;
        //线程任务队列大小
        final int QUEUE_CAPACITY = 500;
        //空闲线程的存活时间.默认情况下核心线程不会退出
        final int KEEP_ALIVE_TIME = 15;
        executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(QUEUE_CAPACITY), GENERAL_THREAD_FACTORY, new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    /**
     * 用户可自定义异步执行时候的线程池
     * @param executor
     */
    public static void setExecutor(Executor executor) {
        GeneralDelayedQueueNewExecute.executor = executor;
    }

    /**
     * 添加要执行的任务方法
     *
     * @param task              延时队列实体
     * @param retryTimeTypeator 重试时间的类型
     */
    public static <T> GeneralResult<T> run(GeneralDelayedQueue task, RetryTimeTypeable retryTimeTypeator) {
        String id = task.getId();
        DelayQueue<GeneralDelayedQueue> queue = new DelayQueue<>();
        delayQueueMap.put(id, queue);
        taskMap.put(id, task);
        retryTimeTypeableMap.put(id, retryTimeTypeator);
        CountDownLatch countDownLatch = new CountDownLatch(task.getMaxExecuteNum());
        countDownLatchMap.put(id, countDownLatch);
        setExpireTime(task);
        resultListMap.put(id, new ArrayList<>());
        queue.offer(task);
        return GeneralDelayedQueueNewExecute.execute(task);
    }

    public static <T> GeneralResult<T> run(GeneralDelayedQueue task) {
        return run(task, DefaultRetryTimeTypeator.FixDelayedRetryTimeTypeator());
    }

    /**
     * 异步执行方法，可以单独为某任务根据传入一个线程池执行
     *
     * @param task
     * @param retryTimeTypeator
     * @param executor
     */
    public static void asyncRun(GeneralDelayedQueue task, RetryTimeTypeable retryTimeTypeator, Executor executor) {
        executor.execute(() -> {
            run(task, retryTimeTypeator);
        });
    }

    public static void asyncRun(GeneralDelayedQueue task, RetryTimeTypeable retryTimeTypeator) {
        executor.execute(() -> {
            run(task, retryTimeTypeator);
        });
    }

    public static void asyncRun(GeneralDelayedQueue task) {
        executor.execute(() -> {
            run(task);
        });
    }

    private static <T> GeneralResult<T> execute(GeneralDelayedQueue task) {

        GeneralResult<T> result = GeneralResult.fail();
        String id = task.getId();
        RetryTimeTypeable retryTimeTypeator = retryTimeTypeableMap.get(id);
        List<GeneralResult<T>> resultList = resultListMap.get(id);
        CountDownLatch countDownLatch = countDownLatchMap.get(id);
        DelayQueue<GeneralDelayedQueue> queue = delayQueueMap.get(id);
        try {
            result = task.getConsumerable().run(queue.take());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            task.setLastTime(retryTimeTypeator.getTime(task));
            //添加执行结果
            resultList.add(result);
            countDownLatch.countDown();
            //延时执行
            if (task.getCurrExecuteNum() < task.getMaxExecuteNum() - 1 && !result.isSuccess()) {
                task.setCurrExecuteNum(task.getCurrExecuteNum() + 1);
                setExpireTime(task);
                queue.offer(task);
                execute(task);
            } else {
                while ((countDownLatch.getCount()) > 0) {
                    countDownLatch.countDown();
                }
            }
            if (task.isKeepResults()) {
                clearByTaskId(task.getId());
            }
        }
        return result;
    }


    private static void setExpireTime(GeneralDelayedQueue task) {
        long expireTime = 0;
        RetryTimeTypeable retryTimeTypeator = retryTimeTypeableMap.get(task.getId());
        if (task.getCurrExecuteNum() == 0) {
            expireTime = TimeUnit.NANOSECONDS.convert(
                    task.getDelayedTime(), task.getTimeUnit()) + System.nanoTime();
        } else {
            expireTime = TimeUnit.NANOSECONDS.convert(
                    retryTimeTypeator.getTime(task), task.getTimeUnit()) + System.nanoTime();
        }
        task.setExpireTime(expireTime);
    }

    /**
     * 得到执行结果
     *
     * @param fastReturn 立即返回 true 代表立即返回， false 代表必须等到最大执行次数后返回（list.size = maxExecuteNum）
     * @return 执行结果列表
     */
    public static <T> List<GeneralResult<T>> getResultList(GeneralDelayedQueue task, boolean fastReturn) {
        try {
            String id = task.getId();
            if (!fastReturn) {
                countDownAwait(id, null, null);
            }
            return resultListMap.get(id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 得到上一次的执行结果(一定能保证获取到结果)
     *
     * @return
     */
    public static <T> GeneralResult<T> getLastResult(GeneralDelayedQueue task) {
        try {
            String id = task.getId();
            CountDownLatch countDownLatch = countDownLatchMap.get(id);
            //保证一定至少执行完成过一次
            if (countDownLatch.getCount() == task.getMaxExecuteNum()) {
                countDownLatch.await();
            }
            List<GeneralResult<T>> resultList = resultListMap.get(id);
            return resultList.get(resultList.size() - 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return GeneralResult.fail();
    }

    /**
     * 得到最终一次的执行结果
     *
     * @return
     */
    public static <T> GeneralResult<T> getFinalResult(GeneralDelayedQueue task) {
        try {
            String id = task.getId();
            countDownAwait(id, null, null);
            List<GeneralResult<T>> resultList = resultListMap.get(id);
            return resultList.get(resultList.size() - 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return GeneralResult.fail();
    }

    /**
     * 得到最终一次的执行结果
     *
     * @param timeOut  超时时间
     * @param timeUnit 时间单位
     * @return 如果超时返回失败
     */
    public static <T> GeneralResult<T> getFinalResult(GeneralDelayedQueue task, long timeOut, TimeUnit timeUnit) {
        try {
            String id = task.getId();
            if (countDownAwait(id, timeOut, timeUnit) == 0) {
                List<GeneralResult<T>> resultList = resultListMap.get(id);
                return resultList.get(resultList.size() - 1);
            } else {
                return GeneralResult.fail("40001", "执行超时，剩余任务正在执行中，无法获取最终执行结果");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return GeneralResult.fail();
    }

    private static long countDownAwait(String id, Long timeOut, TimeUnit timeUnit) throws InterruptedException {
        CountDownLatch countDownLatch = countDownLatchMap.get(id);
        if (timeOut == null) {
            countDownLatch.await();
        } else {
            countDownLatch.await(timeOut, timeUnit);
        }
        return countDownLatch.getCount();
    }

    /**
     * 根据任务id清除任务全部的map信息
     *
     * @param taskId
     */
    private static void clearByTaskId(String taskId) {
        CountDownLatch countDownLatch = countDownLatchMap.get(taskId);
        while (countDownLatch != null && countDownLatch.getCount() > 0) {
            countDownLatch.countDown();
        }
        delayQueueMap.remove(taskId);
        taskMap.remove(taskId);
        countDownLatchMap.remove(taskId);
        taskMap.remove(taskId);
        resultListMap.remove(taskId);
    }
}
