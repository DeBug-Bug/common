package com.b0c0.common.delayedQueue;


import com.b0c0.common.delayedQueue.base.RetryTimeTypeable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @program: springbootdemo
 * @description: 默认的重试时间实现接口
 * @author: lidongsheng
 * @createData: 2020-09-25 19:41
 * @updateAuthor: lidongsheng
 * @updateData: 2020-09-25 19:41
 * @updateContent:
 * @Version: 1.0.0
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 * ************************************************
 * Copyright @ 李东升 2020. All rights reserved
 * ************************************************
 */

public class DefaultRetryTimeTypeator {


    /**
     * 渐进步长 retryTime越大，重试延时时间的间隔时间就会越来越大
     * 栗子 retryTime = 5
     * 第一次重试延时 5 = 0 + 1 * 5
     * 第二次重试延时 15 = 5 + 2 * 5
     * 第三次重试延时  30 = 15 + 3 * 5
     * 第四次重试延时  50 = 30 + 4 * 5
     * ... 第10次重试延时  225
     * ... 第20次重试延时  950
     * ... 第30次重试延时  2175
     * ... 第40次重试延时  3900
     * ... 第50次重试延时  6125
     * @return
     */
    public static RetryTimeTypeable AdvanceStepTimeRetryTimeTypeator() {
        return new AdvanceStepTimeRetryTimeTypeator();
    }

    /**
     * 固定时间
     * 栗子 retryTime = 5
     * 第一次重试延时 5 第二次重试延时 5 第三次重试延时 5
     *
     * @return
     */
    public static RetryTimeTypeable FixDelayedRetryTimeTypeator() {
        return new FixDelayedRetryTimeTypeator();
    }

    /**
     * 固定步长
     * 栗子 retryTime = 5
     * 第一次重试延时 5 第二次重试延时 10 第三次重试延时 15
     *
     * @return
     */
    public static RetryTimeTypeable FixStepTimeRetryTimeTypeator() {
        return new FixStepTimeRetryTimeTypeator();
    }

    /**
     * 斐波那契数列 建议不要超过30
     * CurrExecuteNum = 10  return 55
     * CurrExecuteNum = 20  return 6765
     * CurrExecuteNum = 30  return 832040
     * CurrExecuteNum = 40  return 102334155
     * .....
     * @return
     */
    public static RetryTimeTypeable FibonacciSeriesRetryTimeTypeator() {
        return new FibonacciSeriesRetryTimeTypeator();
    }

    private static class AdvanceStepTimeRetryTimeTypeator implements RetryTimeTypeable {

        @Override
        public long getTime(GeneralDelayedQueue task) {
            return (task.getCurrExecuteNum() == 1 ? 0 : task.getLastTime()) + task.getCurrExecuteNum() * task.getRetryTime();
        }
    }

    private static class FixDelayedRetryTimeTypeator implements RetryTimeTypeable {

        @Override
        public long getTime(GeneralDelayedQueue task) {
            return task.getRetryTime();
        }
    }

    private static class FixStepTimeRetryTimeTypeator implements RetryTimeTypeable {
        @Override
        public long getTime(GeneralDelayedQueue task) {
            return task.getCurrExecuteNum() * task.getRetryTime();
        }
    }

    private static class FibonacciSeriesRetryTimeTypeator implements RetryTimeTypeable {
        @Override
        public long getTime(GeneralDelayedQueue task) {
            int a = 0, b = 1, sum;
            int n = task.getCurrExecuteNum();
            for (int i = 0; i < n; i++) {
                sum = a + b;
                a = b;
                b = sum;
            }
            return a;
        }
    }

    public static void main(String[] args) {
        AdvanceStepTimeRetryTimeTypeator retryTimeTypeator = new AdvanceStepTimeRetryTimeTypeator();
        GeneralDelayedQueue delayedQueue = new GeneralDelayedQueue(
                UUID.randomUUID().toString(),
                null,
                8, 0, 150,TimeUnit.MILLISECONDS);
        for (int i = 0; i < 8; i++) {
            delayedQueue.setCurrExecuteNum(i);
            long time = retryTimeTypeator.getTime(delayedQueue);
            delayedQueue.setLastTime(time);
            System.out.println(time);
        }
    }
}
