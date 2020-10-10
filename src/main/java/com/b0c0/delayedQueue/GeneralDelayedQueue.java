package com.b0c0.delayedQueue;


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
public class GeneralDelayedQueue implements Delayed {

    // 每次请求的唯一id
    private String requestId;
    //主题内容
    private String body;
    //当前的执行次数(可设置此值为maxExecuteNum来达到强制中断之后的重试执行)
    private int currExecuteNum;
    //最大执行次数 如果大于1 表示开启失败重试
    private int maxExecuteNum;
    //延时时间
    private long delayedTime;
    //重试时间
    private long retryTime;
    //过期时间
    private long expireTime;
    //上次的延时时间
    private long lastTime = -1;

    //时间单位
    private TimeUnit timeUnit;


    public String getRequestId() {
        return requestId;
    }

    public String getBody() {
        return body;
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

    /**
     * 完整参数的构造方法
     *
     * @param requestId     唯一标识
     * @param body          主题内容
     * @param maxExecuteNum 最大执行次数
     * @param delayedTime   首次执行延时时间
     * @param retryTime     重试延时时间
     * @param timeUnit      时间单位
     */
    public GeneralDelayedQueue(String requestId, String body, int maxExecuteNum, long delayedTime, long retryTime,TimeUnit timeUnit) {
        this.requestId = requestId;
        this.body = body;
        this.currExecuteNum = 0;
        this.maxExecuteNum = maxExecuteNum;
        this.delayedTime = delayedTime;
        this.retryTime = retryTime;
        this.timeUnit = timeUnit;
    }


    /**
     * 构造方法 默认时间单位秒,自动捕获异常
     *
     * @param requestId     唯一标识
     * @param body          主题内容
     * @param maxExecuteNum 最大执行次数
     * @param delayedTime   首次执行延时时间
     * @param retryTime     重试延时时间
     */
    public GeneralDelayedQueue(String requestId, String body, int maxExecuteNum, long delayedTime, long retryTime) {
        this(requestId, body, maxExecuteNum, delayedTime, retryTime, TimeUnit.SECONDS);
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
