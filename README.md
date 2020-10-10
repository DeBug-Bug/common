

/**
 * @program: springbootdemo
 * @description: 默认的重试时间实现接口
 * @author: lidongsheng
 * @createData: 2020-10-02 18:41
 * @updateAuthor: lidongsheng
 * @updateData: 2020-10-03 11:41
 * @updateContent:
 * @Version: 0.0.2
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
如果确认版本号没有错的话，是0.0.2-SNAPSHOT版本：
```
    <dependency>
        <groupId>com.b0c0</groupId>
        <artifactId>common</artifactId>
        <version>0.0.2-SNAPSHOT</version>
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
@ComponentScan( basePackages ={"其他","其他","com.b0c0.log"})
```


 

# 基础包

### UPDATE LOG:

#### 最新开发版本：0.0.2-SNAPSHOT

#### 0.0.2-SNAPSHOT: 
* 添加延时队列(可自定义延时时间和失败重试-延时步长)
    使用示例：
```
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
```


* 添加通用日志打印注解 @GeneralPrintAOP

    
