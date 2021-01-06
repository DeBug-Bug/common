package com.b0c0.common.delayedQueue;


import com.b0c0.common.delayedQueue.base.GeneralQueueConsumerable;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;


/**
 * @program: springbootdemo
 * @description: 通用延时队列实体
 * @author: lidongsheng
 * @createData: 2020-09-21 14:01
 * @updateAuthor: lidongsheng
 * @updateData: 2020-09-21 14:01
 * @updateContent:
 * @Version: 1.0.0
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 * ************************************************
 * Copyright @ 李东升 2020. All rights reserved
 * ************************************************
 */
public class GeneralDelayedQueue<T> implements Delayed {


    public static class BodyData<T,V>{
        /**
         * 开发者自定义的需要的数据体.
         */
        private T body;

        /**
         * 如果为任务链任务，上一个任务的执行结果会保存到这里，
         */
        private V preResult;

        public T getBody() {
            return body;
        }

        protected void setBody(T body) {
            this.body = body;
        }

        public V getPreResult() {
            return preResult;
        }

        protected void setPreResult(V preResult) {
            this.preResult = preResult;
        }
    }

    //任务的唯一id
    private String id;
    /**
     * 任务的自定义数据体
     */
    private BodyData bodyData;
    /**
     * 任务当前的执行次数(可设置此值为maxExecuteNum来达到强制中断之后的重试执行)
     *
     */
    private int currExecuteNum;
    /**
     * 最大执行次数
     * 此值为1 表示只执行一次，不开启重发
     * 此值大于1 表示开启重发，并且此值为最大执行次数（包含首次执行）。
     */
    private int maxExecuteNum;

    /**
     * 任务的首次执行的延时时间，只有任务的首次执行时会用到此值进行延时执行
     */
    private long delayedTime;

    /**
     * 任务的重发延时时间，重发自定义延时策略会用到此值
     */
    private long retryTime;
    /**
     * 任务的过期时间,任务到达了过期时间就会执行
     * 检测延迟任务是否到期
     */
    private long expireTime;
    /**
     * 上次的延时时间
     */
    private long lastTime = -1;
    /**
     * 时间单位
     */
    private TimeUnit timeUnit;

    /**
     * 执行结果一直保存,可在执行器中随时获取，直至开发人员手动调用删除
     * 注意：如果设置为true了，请务必手动调用GeneralDelayedQueueExecute.clearTask 进行删除。否则任务相关信息将一直存在于内存中
     */
    private boolean keepResults;

    private GeneralQueueConsumerable consumerable;

    public String getId() {
        return id;
    }

    public <T,V>BodyData<T,V> getBodyData() {
        return bodyData;
    }

    public static<T,V> BodyData<T,V> initBodyData(T userData) {
        BodyData<T,V> bodyData= new BodyData<>();
        bodyData.setBody(userData);
        return bodyData;
    }

    public int getCurrExecuteNum() {
        return currExecuteNum;
    }

    public int getMaxExecuteNum() {
        return maxExecuteNum;
    }

    public long getDelayedTime() {
        return delayedTime;
    }

    public long getRetryTime() {
        return retryTime;
    }

    public long getLastTime() {
        return lastTime;
    }

    protected void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    protected void setCurrExecuteNum(int currExecuteNum) {
        this.currExecuteNum = currExecuteNum;
    }

    protected void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public GeneralQueueConsumerable getConsumerable() {
        return consumerable;
    }

    public void setConsumerable(GeneralQueueConsumerable consumerable) {
        this.consumerable = consumerable;
    }

    public boolean isKeepResults() {
        return keepResults;
    }

    /**
     * 完整参数的构造方法
     *
     * @param consumerable  具体任务方法
     * @param id            唯一标识
     * @param userData      主题内容
     * @param keepResults   true表示执行结果一直保存,可在执行器中随时获取，直至开发人员手动调用删除
     * @param maxExecuteNum 最大执行次数
     * @param delayedTime   首次执行延时时间
     * @param retryTime     重试延时时间
     * @param timeUnit      时间单位
     */
    public GeneralDelayedQueue(GeneralQueueConsumerable consumerable,String id, T userData,boolean keepResults, int maxExecuteNum, long delayedTime, long retryTime,TimeUnit timeUnit) {
        this.consumerable = consumerable;
        this.id = id;
        this.bodyData = initBodyData(userData);
        this.keepResults = keepResults;
        this.currExecuteNum = 0;
        this.maxExecuteNum = maxExecuteNum;
        this.delayedTime = delayedTime;
        this.retryTime = retryTime;
        this.timeUnit = timeUnit;

    }


    /**
     * 构造方法 默认时间单位秒,自动捕获异常
     *
     * @param consumerable  具体任务方法
     * @param id            唯一标识
     * @param userData      主题内容
     * @param maxExecuteNum 最大执行次数
     * @param delayedTime   首次执行延时时间
     * @param retryTime     重试延时时间
     */
    public GeneralDelayedQueue(GeneralQueueConsumerable consumerable,String id, T userData, int maxExecuteNum, long delayedTime, long retryTime) {
        this(consumerable,id, userData,false, maxExecuteNum, delayedTime, retryTime, TimeUnit.MILLISECONDS);
    }


    @Override
    public int compareTo(Delayed delayed) {
        long result = this.getDelay(TimeUnit.NANOSECONDS)
                - delayed.getDelay(TimeUnit.NANOSECONDS);
        if (result < 0) {
            return -1;
        } else if (result > 0) {
            return 1;
        } else {
            return 0;
        }
    }


    /**
     * 检测延迟任务是否到期
     * 如果返回的是负数则说明到期否则还没到期
     *
     * @param unit
     * @return
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expireTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

}
