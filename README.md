

/**
 * @program: springbootdemo
 * @description: 默认的重试时间实现接口
 * @author: lidongsheng
 * @createData: 2020-10-02 18:41
 * @updateAuthor: lidongsheng
 * @updateData: 2020-10-24 11:41
 * @updateContent:
 * @Version: 0.0.5
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 * ************************************************
 * Copyright @ 李东升 2020. All rights reserved
 * ************************************************
 */

# 常见问题说明

## 1. 无法拉取SNAPSHOT版本的问题
如果是SNAPSHOT版本，具体说明请自行百度SNAPSHOT和release版本的区别
如果确认版本号没有错的话，例如是0.0.5-SNAPSHOT版本：
```
    <dependency>
        <groupId>com.b0c0</groupId>
        <artifactId>common</artifactId>
        <version>0.0.5-SNAPSHOT</version>
    </dependency>
```
提示无法找到无法拉取的话，请在项目的pom.xml文件中添加如下：
```
    <!-- 提示无法找到无法拉取的话，请在项目的pom.xml（和dependencies同级）文件中加入以下设置 -->
    <repositories>
        <repository>
            <snapshots>
                <enabled>true</enabled>
                <!-- 保持总是拉取最新版本，即使版本一样也会覆盖已经存在的（如果作为稳定开发的话，建议将此值设置为never，
                        如需要最新的时候可再将此值设置为always，再重新拉取。） -->
                <updatePolicy>always</updatePolicy>
            </snapshots>
            <id>snapshots</id>
            <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        </repository>
    </repositories>
```
## 2. 关于@GeneralPrintAOP 日志注解不生效的问题

请在项目启动类上添加

```
@Import({a.class, b.class, GeneralPrintLogAspect.class})
```


 

# 基础包

#### 开发计划

#### 已完成

1. 支持任务单次执行的失败重试，开发者并可自定义配置重试延时时间策略（默认四种：渐进步长、固定时间、固定步长、斐波那契数列）、重试次数(v0.0.6)。
2. 支持查看每次执行结果（包括失败重试的执行结果）(v0.0.6)。
3. 一个执行器统一管理所有任务，现在一个执行器只能管理执行一个任务(v0.0.7)
4. 支持任务自定义顺序完成(流水线完成任务) 例如1 -> 2 -> 3 (v0.0.7)
#### 未完成
3. 区分重复执行和执行失败分离 (放弃，没有太大必要)
   * 支持重复执行（可配置重复执行、失败重试，并且可分别配置重复执行的延时时间和失败重试的延时时间策略。）
4. 支持执行失败可自定义失败执行的逻辑（默认为原逻辑不变） (放弃，没有太大必要)

### UPDATE LOG:

#### 最新稳定版本：0.0.7

#### 最新开发版本：0.0.8-SNAPSHOT

#### 0.0.4: 
* 添加延时队列(可自定义延时时间和失败重试-延时步长)

#### 0.0.6: 
* 支持返回结果（同步异步）

#### 0.0.7:
* 一个执行器统一管理所有任务
* 异步执行结果通过Future获得  
* 支持任务自定义顺序完成(流水线完成任务)

使用示例：
```
package com.b0c0.common.delayedQueue;

import com.b0c0.common.delayedQueue.base.GeneralQueueConsumerable;
import com.b0c0.common.domain.vo.GeneralResultVo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class GeneralDelayedQueueExecuteTestVo {

    private static final Logger logger = Logger.getLogger(GeneralDelayedQueueExecuteTestVo.class.getName());


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
        TestVo t = (TestVo) delayedQueue3.getBodyData().getPreResult();
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

    static class TestConsumer2 implements GeneralQueueConsumerable<TestVo, String> {

        @Override
        public GeneralResultVo<TestVo> run(GeneralDelayedQueue<String> task) {
            String body = task.getBodyData().getBody();
            String requestId = task.getId();
            int currExecuteNum = task.getCurrExecuteNum();
            logger.info("thread ->" + Thread.currentThread().getId() + "time ->" + System.currentTimeMillis() + " 消费延时队列 requestId -> " + requestId + " ,第 -> " + (currExecuteNum + 1) + " 次,body -> " + body);
            TestVo testVo = new TestVo();
            testVo.setA("a");
            testVo.setB("b");
            return GeneralResultVo.success(testVo);
        }
    }

    public static class TestVo {
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
```


* 添加通用日志打印注解 @GeneralPrintAOP

使用此注解务必再项目启动类上加上
```
@Import({GeneralPrintLogAspect.class})
```

    
