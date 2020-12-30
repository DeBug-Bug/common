package com.b0c0.common.delayedQueue;

import com.b0c0.common.delayedQueue.base.GeneralQueueConsumerable;
import com.b0c0.common.utils.GeneralResult;

import java.util.logging.Logger;

public class GeneralDelayedQueueNewExecuteTest {

    private static final Logger logger = Logger.getLogger(GeneralDelayedQueueNewExecuteTest.class.getName());

    @org.junit.Test
    public void run() {
        GeneralDelayedQueue delayedQueue1 = new GeneralDelayedQueue(
                new TestConsumer(), "1", "body", 1, 500, 5);
        GeneralDelayedQueue delayedQueue2 = new GeneralDelayedQueue(
                new TestConsumer(), "2", "body", 3, 10, 2);
        GeneralDelayedQueue delayedQueue3 = new GeneralDelayedQueue(
                new TestConsumer(), "3", "body", 3, 15, 3);
        GeneralDelayedQueue delayedQueue4 = new GeneralDelayedQueue(
                new TestConsumer(), "4", "body", 4, 20, 4);
        GeneralDelayedQueue delayedQueue5 = new GeneralDelayedQueue(
                new TestConsumer(), "5", "body", 5, 30, 5);

        GeneralDelayedQueueNewExecute.asyncRun(delayedQueue1);
        GeneralDelayedQueueNewExecute.asyncRun(delayedQueue2);
        GeneralDelayedQueueNewExecute.asyncRun(delayedQueue3);
        GeneralDelayedQueueNewExecute.asyncRun(delayedQueue4);
        GeneralDelayedQueueNewExecute.asyncRun(delayedQueue5);
    }


    static class TestConsumer implements GeneralQueueConsumerable {

        @Override
        public GeneralResult<String> run(GeneralDelayedQueue task) {
            String body = task.getBody();
            String requestId = task.getId();
            int currExecuteNum = task.getCurrExecuteNum();
            logger.info("thread ->"+Thread.currentThread().getId()+"time ->" + System.currentTimeMillis() + " 消费延时队列 requestId -> " + requestId + " ,第 -> " + (currExecuteNum + 1) + " 次,body -> " + body);
//            return GeneralResult.success(currExecuteNum + 1);
//            return GeneralResult.success(task.getId());
            return GeneralResult.fail();
        }
    }
}