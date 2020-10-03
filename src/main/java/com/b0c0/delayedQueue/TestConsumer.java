package com.b0c0.delayedQueue;

import com.b0c0.delayedQueue.base.GeneralQueueConsumerable;

import java.util.UUID;

/**
 * @program: springbootdemo
 * @description: 测试延时队列消费者
 * @author: lidongsheng
 * @createData: 2020-09-21 16:01
 * @updateAuthor: lidongsheng
 * @updateData: 2020-09-21 16:01
 * @updateContent:
 * @Version: 1.0.0
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 * ************************************************
 * Copyright @ 李东升 2020. All rights reserved
 * ************************************************
 */

public class TestConsumer implements GeneralQueueConsumerable {


    @Override
    public boolean run(GeneralDelayedQueue task) {
        String body = task.getBody();
        String requestId = task.getRequestId();
        int currExecuteNum = task.getCurrExecuteNum();
        System.out.println("消费延时队列 requestId -> "+requestId+" ,第 -> "+currExecuteNum + 1+" 次,body -> "+body);
        return true;
    }

    public static void main(String[] args) {

        GeneralDelayedQueue delayedQueue = new GeneralDelayedQueue(
                UUID.randomUUID().toString(),
                "jsonbody",
                4, 5, 5);

        new GeneralDelayedQueueExecute(
                new TestConsumer(),
                delayedQueue,
                DefaultRetryTimeTypeator.AdvanceStepTimeRetryTimeTypeator()).run();
    }

}
