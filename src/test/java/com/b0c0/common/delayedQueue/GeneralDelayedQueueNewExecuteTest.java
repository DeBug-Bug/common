package com.b0c0.common.delayedQueue;

import com.b0c0.common.delayedQueue.base.GeneralQueueConsumerable;
import com.b0c0.common.domain.vo.GeneralResultVo;

import java.util.logging.Logger;

public class GeneralDelayedQueueNewExecuteTest {

    private static final Logger logger = Logger.getLogger(GeneralDelayedQueueNewExecuteTest.class.getName());


    @org.junit.Test
    public void run() {
        GeneralDelayedQueue delayedQueue1 = new GeneralDelayedQueue(
                new TestConsumer1(), "1", "body", 1, 500, 50);
        GeneralDelayedQueue delayedQueue2 = new GeneralDelayedQueue(
                new TestConsumer1(), "2", "body", 3, 100, 100);
        GeneralDelayedQueue delayedQueue3 = new GeneralDelayedQueue(
                new TestConsumer1(), "3", "body", 3, 150, 150);

        GeneralDelayedQueueNewExecute.run(delayedQueue1);
        GeneralDelayedQueueNewExecute.run(delayedQueue2);
        GeneralDelayedQueueNewExecute.run(delayedQueue3);
    }


    @org.junit.Test
    public void runAsync() {
        GeneralDelayedQueue delayedQueue1 = new GeneralDelayedQueue(
                new TestConsumer1(), "1", "body", 1, 500, 5);
        GeneralDelayedQueue delayedQueue2 = new GeneralDelayedQueue(
                new TestConsumer1(), "2", "body", 3, 10, 2);
        GeneralDelayedQueue delayedQueue3 = new GeneralDelayedQueue(
                new TestConsumer1(), "3", "body", 3, 15, 3);

        GeneralDelayedQueueNewExecute.runAsync(delayedQueue1);
        GeneralDelayedQueueNewExecute.runAsync(delayedQueue2);
        GeneralDelayedQueueNewExecute.runAsync(delayedQueue3);
        try {
            Thread.sleep(500000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    static class TestConsumer1 implements GeneralQueueConsumerable {

        @Override
        public GeneralResultVo<String> run(GeneralDelayedQueue task) {
            String body = (String) task.getBodyData().getBody();
            String requestId = task.getId();
            int currExecuteNum = task.getCurrExecuteNum();
            logger.info("thread ->"+Thread.currentThread().getId()+"time ->" + System.currentTimeMillis() + " 消费延时队列 requestId -> " + requestId + " ,第 -> " + (currExecuteNum + 1) + " 次,body -> " + body);
            return GeneralResultVo.fail();
        }
    }
}