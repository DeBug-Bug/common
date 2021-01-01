package com.b0c0.common.delayedQueue;


import com.b0c0.common.delayedQueue.base.RetryTimeTypeable;
import com.b0c0.common.domain.vo.GeneralResultCodeEnum;
import com.b0c0.common.domain.vo.GeneralResultVo;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;


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
public class GeneralDelayedQueueExecute {

    private static final Logger logger = Logger.getLogger(GeneralDelayedQueueExecute.class.getName());

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
    private static ExecutorService executor;

    static {
        final ThreadFactory GENERAL_THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("com.b0c0.commom.delayedQueue-pool-general-%d").build();
        //核心线程池大小
        final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() << 1 + 1;
        //最大线程池大小
        final int MAX_POOL_SIZE = CORE_POOL_SIZE << 1;
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
     *
     * @param executor
     */
    public static void setExecutor(ExecutorService executor) {
        GeneralDelayedQueueExecute.executor = executor;
    }

    /**
     * 执行方法
     *
     * @param task              具体任务
     * @param retryTimeTypeator 重试延时时间策略
     */
    public static <T> GeneralResultVo<T> run(GeneralDelayedQueue task, RetryTimeTypeable retryTimeTypeator) {
        DelayQueue<GeneralDelayedQueue> queue = new DelayQueue<>();
        initTask(task, retryTimeTypeator, queue);
        return execute(task);
    }

    public static <T> GeneralResultVo<T> run(GeneralDelayedQueue task) {
        return run(task, DefaultRetryTimeTypeator.FixDelayedRetryTimeTypeator());
    }

    /**
     * 异步执行方法，可以单独为某任务根据传入一个线程池执行
     *
     * @param task              具体任务
     * @param retryTimeTypeator 重试延时时间策略
     * @param executor          用户自定义线程池
     */
    public static <T> Future<GeneralResultVo<T>> runAsync(GeneralDelayedQueue task, RetryTimeTypeable retryTimeTypeator, ExecutorService executor) {
        return executor.submit(() -> run(task, retryTimeTypeator));
    }

    /**
     * 异步执行方法 默认内置线程池
     *
     * @param task              具体任务
     * @param retryTimeTypeator 重试延时时间策略
     */
    public static <T> Future<GeneralResultVo<T>> runAsync(GeneralDelayedQueue task, RetryTimeTypeable retryTimeTypeator) {
        return executor.submit(() -> run(task, retryTimeTypeator));
    }

    /**
     * 异步执行方法  默认内置线程池
     *
     * @param task 具体任务
     */
    public static <T> Future<GeneralResultVo<T>> runAsync(GeneralDelayedQueue task) {
        return executor.submit(() -> run(task));
    }

    /**
     * 任务链的执行方法 自定义顺序完成(流水线完成任务) 例如A -> B -> C
     * 并且任务的执行结果会自动传递给下一任务。比如A任务的执行结果，会传递给B任务。
     *
     * @param tasks              具体任务list集合，会按照集合的添加顺序来流水线顺序执行任务
     * @param retryTimeTypeators 重试延时时间策略
     * @param <T>
     * @return 任务链执行返回值为：返回的为最后一个运行任务的返回值。
     */
    public static <T> GeneralResultVo<T> runLine(List<GeneralDelayedQueue> tasks, List<RetryTimeTypeable> retryTimeTypeators) {

        if (tasks == null || tasks.isEmpty() || retryTimeTypeators == null || retryTimeTypeators.isEmpty()) {
            return GeneralResultVo.fail(GeneralResultCodeEnum.PARAM_ERROR.getCode(), "任务集合和重试延时时间策略集合不能为空");
        }
        int taskSize = tasks.size();
        if (taskSize != retryTimeTypeators.size()) {
            return GeneralResultVo.fail(GeneralResultCodeEnum.PARAM_ERROR.getCode(), "任务集合和重试延时时间策略集合大小不一致，无法相互对应");
        }
        DelayQueue<GeneralDelayedQueue> queue = new DelayQueue<>();
        GeneralResultVo<T> resultVo = GeneralResultVo.fail();
        for (int i = 0; i < taskSize; i++) {
            initTask(tasks.get(i), retryTimeTypeators.get(i), queue);
            resultVo = execute(tasks.get(i));
            if (resultVo.isSuccess()) {
                if (i != 0 && i != taskSize - 1) {
                    tasks.get(i + 1).getBodyData().setPreResult(resultVo.getReslutData());
                }
            } else {
                break;
            }
        }
        return resultVo;
    }

    public static <T> GeneralResultVo<T> runLine(List<GeneralDelayedQueue> tasks) {
        int taskSize = tasks.size();
        List<RetryTimeTypeable> retryTimeTypeators = new ArrayList<>();
        for (int i = 0; i < taskSize; i++) {
            retryTimeTypeators.add(DefaultRetryTimeTypeator.FixDelayedRetryTimeTypeator());
        }
        return runLine(tasks, retryTimeTypeators);
    }

    /**
     * 异步执行任务链方法，可以单独为某任务根据传入一个线程池执行
     *
     * @param tasks              具体任务
     * @param retryTimeTypeators 重试延时时间策略
     * @param executor           用户自定义线程池
     */
    public static <T> Future<GeneralResultVo<T>> runLinesync(List<GeneralDelayedQueue> tasks, List<RetryTimeTypeable> retryTimeTypeators, ExecutorService executor) {
        return executor.submit(() -> runLine(tasks, retryTimeTypeators));
    }

    /**
     * 异步执行任务链方法 默认内置线程池
     *
     * @param tasks              具体任务
     * @param retryTimeTypeators 重试延时时间策略
     */
    public static <T> Future<GeneralResultVo<T>> runLinesync(List<GeneralDelayedQueue> tasks, List<RetryTimeTypeable> retryTimeTypeators) {
        return executor.submit(() -> runLine(tasks, retryTimeTypeators));
    }

    /**
     * 异步执行任务链方法  默认内置线程池
     *
     * @param tasks 具体任务
     */
    public static <T> Future<GeneralResultVo<T>> runLinesync(List<GeneralDelayedQueue> tasks) {
        return executor.submit(() -> runLine(tasks));
    }

    private static <T> GeneralResultVo<T> execute(GeneralDelayedQueue task) {

        GeneralResultVo<T> result = GeneralResultVo.fail();
        String id = task.getId();
        RetryTimeTypeable retryTimeTypeator = retryTimeTypeableMap.get(id);
        List<GeneralResultVo<T>> resultList = resultListMap.get(id);
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
            if (!task.isKeepResults()) {
                clearTask(task.getId());
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
     * 得到全部的执行结果
     *
     * @param fastReturn 立即返回 true 代表立即返回， false 代表必须等到最大执行次数后返回（list.size = maxExecuteNum）
     * @return 执行结果列表
     */
    public static <T> List<GeneralResultVo<T>> getResultList(GeneralDelayedQueue task, boolean fastReturn) {
        try {
            String id = task.getId();
            if (!fastReturn) {
                awaitCountDown(id, null, null);
            }
            return resultListMap.get(id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 等待任务全部完成
     *
     * @param id       任务id
     * @param timeOut  等待超时时间
     * @param timeUnit 时间单位
     * @return countDownLatch当前计数值
     * @throws InterruptedException
     */
    private static long awaitCountDown(String id, Long timeOut, TimeUnit timeUnit) throws InterruptedException {
        CountDownLatch countDownLatch = countDownLatchMap.get(id);
        if (timeOut == null) {
            countDownLatch.await();
        } else {
            countDownLatch.await(timeOut, timeUnit);
        }
        return countDownLatch.getCount();
    }

    /**
     * 根据任务初始化任务信息
     *
     * @param task
     * @param retryTimeTypeator
     */
    private static void initTask(GeneralDelayedQueue task, RetryTimeTypeable retryTimeTypeator, DelayQueue<GeneralDelayedQueue> queue) {
        String id = task.getId();
        delayQueueMap.put(id, queue);
        taskMap.put(id, task);
        retryTimeTypeableMap.put(id, retryTimeTypeator);
        CountDownLatch countDownLatch = new CountDownLatch(task.getMaxExecuteNum());
        countDownLatchMap.put(id, countDownLatch);
        setExpireTime(task);
        resultListMap.put(id, new ArrayList<>());
        queue.offer(task);
    }


    /**
     * 根据任务id清除任务全部的map信息
     * GeneralDelayedQueue的keepResults如果设置为true了，请务必手动调用此方法进行删除。否则任务相关信息将一直存在于内存中
     *
     * @param taskId 任务id
     */
    public static void clearTask(String taskId) {
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
