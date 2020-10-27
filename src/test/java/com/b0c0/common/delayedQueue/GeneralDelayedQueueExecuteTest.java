package com.b0c0.common.delayedQueue;


import com.b0c0.common.delayedQueue.base.GeneralQueueConsumerable;
import com.b0c0.common.log.GeneralPrintLogAspect;
import com.b0c0.common.utils.GeneralResult;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GeneralDelayedQueueExecuteTest {

    private static final Logger logger = Logger.getLogger(GeneralDelayedQueueExecuteTest.class.getName());

    public static void main(String[] args) {

        GeneralDelayedQueue delayedQueue = new GeneralDelayedQueue(
                UUID.randomUUID().toString(),
                "jsonbody",
                4, 0, 200, TimeUnit.MILLISECONDS);
        GeneralDelayedQueueExecute delayedQueueExecute = new GeneralDelayedQueueExecute(
                new TestConsumer(),
                delayedQueue,
                DefaultRetryTimeTypeator.AdvanceStepTimeRetryTimeTypeator());
        delayedQueueExecute.run();
        GeneralResult generalResult = delayedQueueExecute.getFinalResult();
        System.out.println(generalResult.getReslutData());

    }

    static class TestConsumer implements GeneralQueueConsumerable {

        @Override
        public GeneralResult<Integer> run(GeneralDelayedQueue task) {
            String body = task.getBody();
            String requestId = task.getRequestId();
            int currExecuteNum = task.getCurrExecuteNum();
            logger.info("time ->"+System.currentTimeMillis()+" 消费延时队列 requestId -> " + requestId + " ,第 -> " + (currExecuteNum + 1) + " 次,body -> " + body);
//            return GeneralResult.success(currExecuteNum + 1);
            return GeneralResult.fail();
        }
    }
}