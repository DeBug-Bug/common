package com.b0c0.common.delayedQueue;


import com.b0c0.common.delayedQueue.base.GeneralQueueConsumerable;
import com.b0c0.common.delayedQueue.base.RetryTimeTypeable;
import com.b0c0.common.factory.ThreadPoolFactory;
import com.b0c0.common.utils.GeneralResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class GeneralDelayedQueueNewExecute{

    //延时队列
    private Map<String,DelayQueue<GeneralDelayedQueue>> delayQueueMap;
    //延时队列主题信息
    private Map<String,GeneralDelayedQueue> taskMap;
    //重试时间的具体实现
    private Map<String,RetryTimeTypeable> retryTimeTypeableMap;
    //用来保证程序执行完成之后才能获取到执行结果
    private Map<String,CountDownLatch> countDownLatchMap;
    //存储每次执行的具体结果信息
    private Map<String,List<GeneralResult>> resultListMap;

    /**
     * 添加要执行的任务方法
     * @param task 延时队列实体
     * @param retryTimeTypeator 重试时间的类型
     */
    public GeneralResult run(GeneralDelayedQueue task, RetryTimeTypeable retryTimeTypeator){
        String  requestId= task.getRequestId();
        DelayQueue<GeneralDelayedQueue> queue = new DelayQueue<>();
        queue.offer(task);
        delayQueueMap.put(requestId,queue);
        taskMap.put(requestId,task);
        retryTimeTypeableMap.put(requestId,retryTimeTypeator);
        CountDownLatch countDownLatch = new CountDownLatch(task.getMaxExecuteNum());
        countDownLatchMap.put(requestId,countDownLatch);
        setExpireTime(task);
        resultListMap.put(requestId,new ArrayList<>());
        this.execute(task);
        return this.getFinalResult(task);
    }

    public GeneralResult run(GeneralDelayedQueue task){
        return run(task, DefaultRetryTimeTypeator.FixDelayedRetryTimeTypeator());
    }

    public void asyncRun(GeneralDelayedQueue task,RetryTimeTypeable retryTimeTypeator){
        ThreadPoolFactory.getDelayedQueueThreadExecutor().execute(()->{
            this.run(task,retryTimeTypeator);
        });
    }

    public void asyncRun(GeneralDelayedQueue task){
        ThreadPoolFactory.getDelayedQueueThreadExecutor().execute(()->{
            this.run(task);
        });
    }

    private void execute(GeneralDelayedQueue task){

        GeneralResult result = GeneralResult.fail();
        String requestId = task.getRequestId();
        RetryTimeTypeable retryTimeTypeator = retryTimeTypeableMap.get(requestId);
        List<GeneralResult> resultList = resultListMap.get(requestId);
        CountDownLatch countDownLatch = countDownLatchMap.get(requestId);
        DelayQueue<GeneralDelayedQueue> queue = delayQueueMap.get(requestId);
        try {
            result = task.getConsumerable().run(taskMap.get(requestId));
            task.setLastTime(retryTimeTypeator.getTime(task));
            //添加执行结果
            resultList.add(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
            if (task.getCurrExecuteNum() < task.getMaxExecuteNum() - 1 && !result.isSuccess()) {
                task.setCurrExecuteNum(task.getCurrExecuteNum() + 1);
                setExpireTime(task);
                queue.offer(task);
                this.execute(task);
            } else {
                while ((countDownLatch.getCount()) > 0) {
                    countDownLatch.countDown();
                }
            }
        }
    }


    private void setExpireTime(GeneralDelayedQueue task) {
        long expireTime = 0;
        RetryTimeTypeable retryTimeTypeator = retryTimeTypeableMap.get(task.getRequestId());
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
    public List<GeneralResult> getResultList(GeneralDelayedQueue task,boolean fastReturn) {
        try {
            String requestId = task.getRequestId();
            if (!fastReturn) {
                countDownAwait(requestId,null,null);
            }
            return resultListMap.get(requestId);
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
    public GeneralResult getLastResult(GeneralDelayedQueue task) {
        try {
            String requestId = task.getRequestId();
            CountDownLatch countDownLatch = countDownLatchMap.get(requestId);
            //保证一定至少执行完成过一次
            if (countDownLatch.getCount() == task.getMaxExecuteNum()) {
                countDownLatch.await();
            }
            List<GeneralResult> resultList = resultListMap.get(requestId);
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
    public GeneralResult getFinalResult(GeneralDelayedQueue task) {
        try {
            String requestId = task.getRequestId();
            countDownAwait(requestId,null,null);
            List<GeneralResult> resultList = resultListMap.get(requestId);
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
    public GeneralResult getFinalResult(GeneralDelayedQueue task,long timeOut, TimeUnit timeUnit) {
        try {
            String requestId = task.getRequestId();
            if (countDownAwait(requestId,timeOut,timeUnit) == 0) {
                List<GeneralResult> resultList = resultListMap.get(requestId);
                return resultList.get(resultList.size() - 1);
            }else {
                return GeneralResult.fail("40001","执行超时，剩余任务正在执行中，无法获取最终执行结果");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return GeneralResult.fail();
    }

    private long countDownAwait(String requestId,Long timeOut,TimeUnit timeUnit) throws InterruptedException {
        CountDownLatch countDownLatch = countDownLatchMap.get(requestId);
        if(timeOut == null){
            countDownLatch.await();
        }else{
            countDownLatch.await(timeOut, timeUnit);
        }
        return countDownLatch.getCount();
    }

}
