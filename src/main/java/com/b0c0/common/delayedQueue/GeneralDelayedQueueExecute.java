package com.b0c0.common.delayedQueue;


import com.b0c0.common.delayedQueue.base.GeneralQueueConsumerable;
import com.b0c0.common.delayedQueue.base.RetryTimeTypeable;
import com.b0c0.common.utils.GeneralResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;


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
public class GeneralDelayedQueueExecute implements Runnable {


    //队列消费者
    private GeneralQueueConsumerable consumer;

    //延时队列
    private DelayQueue<GeneralDelayedQueue> queue = new DelayQueue<>();

    //延时队列实体
    private GeneralDelayedQueue task;

    //重试时间的具体实现
    private RetryTimeTypeable retryTimeTypeator;

    //用来保证程序执行完成之后才能获取到执行结果
    private CountDownLatch countDownLatch;

    //存储每次执行的具体结果信息
    private List<GeneralResult> resultList = new ArrayList<>();


    /**
     * 构造方法
     *
     * @param consumer 具体的消费者
     * @param task     延时队列实体
     */
    public GeneralDelayedQueueExecute(GeneralQueueConsumerable consumer, GeneralDelayedQueue task, RetryTimeTypeable retryTimeTypeator) {
        this.consumer = consumer;
        this.task = task;
        this.retryTimeTypeator = retryTimeTypeator;
        countDownLatch = new CountDownLatch(task.getMaxExecuteNum());
        setExpireTime();
        queue.offer(task);
    }

    /**
     * 构造方法(默认为FixDelayedRetryTimeTypeator延时重试方法)
     *
     * @param consumer 具体的消费者
     * @param task     延时队列实体
     */
    public GeneralDelayedQueueExecute(GeneralQueueConsumerable consumer, GeneralDelayedQueue task) {
        this(consumer, task, DefaultRetryTimeTypeator.FixDelayedRetryTimeTypeator());
    }

    @Override
    public void run() {
        GeneralResult result = GeneralResult.fail();
        try {
            result = consumer.run(queue.take());
            task.setLastTime(retryTimeTypeator.getTime(task));
            //添加执行结果
            resultList.add(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
            if (task.getCurrExecuteNum() < task.getMaxExecuteNum() - 1 && !result.isSuccess()) {
                task.setCurrExecuteNum(task.getCurrExecuteNum() + 1);
                setExpireTime();
                queue.offer(task);
                this.run();
            } else {
                while ((countDownLatch.getCount()) > 0) {
                    countDownLatch.countDown();
                }
            }
        }
    }

    private void setExpireTime() {
        long expireTime = 0;
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
    public List<GeneralResult> getResultList(boolean fastReturn) {
        try {
            if (!fastReturn) {
                countDownLatch.await();
            }
            return resultList;
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
    public GeneralResult getLastResult() {
        try {
            //保证一定至少执行完成过一次
            if (countDownLatch.getCount() == task.getMaxExecuteNum()) {
                countDownLatch.await();
            }
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
    public GeneralResult getFinalResult() {
        try {
            countDownLatch.await();
            return resultList.get(resultList.size() - 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return GeneralResult.fail();
    }

    /**
     * 得到最终一次的执行结果 超时时间
     *
     * @param timeOut  超时时间
     * @param timeUnit 时间单位
     * @return
     */
    public GeneralResult getFinalResult(long timeOut, TimeUnit timeUnit) {
        try {
            countDownLatch.await(timeOut, timeUnit);
            if (countDownLatch.getCount() == 0) {
                return resultList.get(resultList.size() - 1);
            }else {
                return GeneralResult.fail("40001","执行超时，剩余任务正在执行中，无法获取最终执行结果");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return GeneralResult.fail();
    }

}
