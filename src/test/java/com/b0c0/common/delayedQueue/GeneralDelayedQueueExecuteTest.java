package com.b0c0.common.delayedQueue;


import com.b0c0.common.delayedQueue.base.GeneralQueueConsumerable;

import java.util.UUID;

public class GeneralDelayedQueueExecuteTest {

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

    static class TestConsumer implements GeneralQueueConsumerable {

        @Override
        public boolean run(GeneralDelayedQueue task) {
            String body = task.getBody();
            String requestId = task.getRequestId();
            int currExecuteNum = task.getCurrExecuteNum();
            System.out.println("消费延时队列 requestId -> " + requestId + " ,第 -> " + (currExecuteNum + 1) + " 次,body -> " + body);
            return false;
        }
    }
}