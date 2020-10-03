package com.b0c0.delayedQueue;


import com.b0c0.delayedQueue.base.GeneralQueueConsumerable;
import com.b0c0.delayedQueue.base.RetryTimeTypeable;

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
public class GeneralDelayedQueueExecute implements Runnable {


    //队列消费者
    private GeneralQueueConsumerable consumer;

    //延时队列
    private DelayQueue<GeneralDelayedQueue> queue = new DelayQueue<>();

    //延时队列实体
    private GeneralDelayedQueue task;

    //重试时间的具体实现
    private RetryTimeTypeable retryTimeTypeator;

    /**
     * 构造方法(默认为FixDelayedRetryTimeTypeator延时重试方法)
     *
     * @param consumer 具体的消费者
     * @param task     延时队列实体
     */
    public GeneralDelayedQueueExecute(GeneralQueueConsumerable consumer, GeneralDelayedQueue task) {
        this.consumer = consumer;
        this.task = task;
        this.retryTimeTypeator = DefaultRetryTimeTypeator.FixDelayedRetryTimeTypeator();
        setExpireTime();
        queue.offer(task);
    }

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
        setExpireTime();
        queue.offer(task);
    }

    @Override
    public void run() {
        try {
            consumer.run(queue.take());
            task.setLastTime(retryTimeTypeator.getTime(task));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (task.getCurrExecuteNum() < task.getMaxExecuteNum()) {
                task.setCurrExecuteNum(task.getCurrExecuteNum() + 1);
                setExpireTime();
                queue.offer(task);
                this.run();
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

}
