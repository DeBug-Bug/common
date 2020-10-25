package com.b0c0.common.delayedQueue;


import com.b0c0.common.delayedQueue.base.GeneralQueueConsumerable;
import com.b0c0.common.utils.GeneralResult;

import java.util.UUID;

public class GeneralDelayedQueueExecuteTest {

    public static void main(String[] args) {

        GeneralDelayedQueue delayedQueue = new GeneralDelayedQueue(
                UUID.randomUUID().toString(),
                "jsonbody",
                4, 5, 5);
        GeneralDelayedQueueExecute delayedQueueExecute = new GeneralDelayedQueueExecute(
                new TestConsumer(),
                delayedQueue,
                DefaultRetryTimeTypeator.FixDelayedRetryTimeTypeator());
        delayedQueueExecute.run();
        GeneralResult generalResult = delayedQueueExecute.getLastResult();
        System.out.println(generalResult.getReslutData());

    }

    static class TestConsumer implements GeneralQueueConsumerable {

        @Override
        public GeneralResult<Integer> run(GeneralDelayedQueue task) {
            String body = task.getBody();
            String requestId = task.getRequestId();
            int currExecuteNum = task.getCurrExecuteNum();
            System.out.println("消费延时队列 requestId -> " + requestId + " ,第 -> " + (currExecuteNum + 1) + " 次,body -> " + body);
            return GeneralResult.success(currExecuteNum + 1);
        }
    }
}