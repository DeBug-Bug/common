package com.b0c0.common.delayedQueue;

import com.b0c0.common.delayedQueue.base.GeneralQueueConsumerable;
import com.b0c0.common.domain.vo.GeneralResultVo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class GeneralDelayedQueueExecuteTest {

    private static final Logger logger = Logger.getLogger(GeneralDelayedQueueExecuteTest.class.getName());


    @org.junit.Test
    public void run() {
        GeneralDelayedQueue delayedQueue1 = new GeneralDelayedQueue(
                new TestConsumer1(), "1", "body", 1, 500, 50);
        GeneralDelayedQueue delayedQueue2 = new GeneralDelayedQueue(
                new TestConsumer1(), "2", "body", 3, 100, 100);
        GeneralDelayedQueue delayedQueue3 = new GeneralDelayedQueue(
                new TestConsumer1(), "3", "body", 3, 150, 150);

        GeneralDelayedQueueExecute.run(delayedQueue1);
        GeneralDelayedQueueExecute.run(delayedQueue2);
        GeneralDelayedQueueExecute.run(delayedQueue3);
    }


    @org.junit.Test
    public void runAsync() throws ExecutionException, InterruptedException {
        GeneralDelayedQueue delayedQueue1 = new GeneralDelayedQueue(
                new TestConsumer1(), "1", "body", 5, 500, 5);
        GeneralDelayedQueue delayedQueue2 = new GeneralDelayedQueue(
                new TestConsumer1(), "2", "body", 3, 10000, 2);
        GeneralDelayedQueue delayedQueue3 = new GeneralDelayedQueue(
                new TestConsumer1(), "3", "body", 3, 1500, 300);
        Future<GeneralResultVo<String>> future1 = GeneralDelayedQueueExecute.runAsync(delayedQueue1);
        Future<GeneralResultVo<String>> future2 = GeneralDelayedQueueExecute.runAsync(delayedQueue2);
        Future<GeneralResultVo<String>> future3 = GeneralDelayedQueueExecute.runAsync(delayedQueue3);
        System.out.println("time ->" + System.currentTimeMillis() + " future1:" + future1.get().getReslutData());
        System.out.println("time ->" + System.currentTimeMillis() + " future2:" + future2.get().getReslutData());
        System.out.println("time ->" + System.currentTimeMillis() + " future3:" + future3.get().getReslutData());

    }

    @org.junit.Test
    public void runLine() {
        GeneralDelayedQueue delayedQueue1 = new GeneralDelayedQueue(
                new TestConsumer1(), "1", "body", 2, 500, 5);
        GeneralDelayedQueue delayedQueue2 = new GeneralDelayedQueue(
                new TestConsumer2(), "2", "body", 3, 10, 2);
        GeneralDelayedQueue delayedQueue3 = new GeneralDelayedQueue(
                new TestConsumer1(), "3", "body", 3, 600, 100);
        List<GeneralDelayedQueue> list = new ArrayList<>();
        list.add(delayedQueue1);
        list.add(delayedQueue2);
        list.add(delayedQueue3);
        GeneralResultVo<String> a = GeneralDelayedQueueExecute.runLine(list);
        Test t = (Test) delayedQueue3.getBodyData().getPreResult();
        System.out.println(t.getA());
        System.out.println(a.getReslutData());

    }


    static class TestConsumer1 implements GeneralQueueConsumerable<String, String> {

        @Override
        public GeneralResultVo<String> run(GeneralDelayedQueue<String> task) {
            String body = task.getBodyData().getBody();
            String requestId = task.getId();
            int currExecuteNum = task.getCurrExecuteNum();
            logger.info("thread ->" + Thread.currentThread().getId() + " time ->" + System.currentTimeMillis() + " 消费延时队列 requestId -> " + requestId + " ,第 -> " + (currExecuteNum + 1) + " 次,body -> " + body);
            if (task.getId().equals("3")) {
                return GeneralResultVo.fail();
            } else {
                return GeneralResultVo.success("test");
            }
        }
    }

    static class TestConsumer2 implements GeneralQueueConsumerable<Test, String> {

        @Override
        public GeneralResultVo<Test> run(GeneralDelayedQueue<String> task) {
            String body = task.getBodyData().getBody();
            String requestId = task.getId();
            int currExecuteNum = task.getCurrExecuteNum();
            logger.info("thread ->" + Thread.currentThread().getId() + "time ->" + System.currentTimeMillis() + " 消费延时队列 requestId -> " + requestId + " ,第 -> " + (currExecuteNum + 1) + " 次,body -> " + body);
            Test test = new Test();
            test.setA("a");
            test.setB("b");
            return GeneralResultVo.success(test);
        }
    }

    public static class Test {
        private String a;
        private String b;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }
    }


}