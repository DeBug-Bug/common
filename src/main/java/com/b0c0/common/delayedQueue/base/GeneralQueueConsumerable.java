package com.b0c0.common.delayedQueue.base;


import com.b0c0.common.delayedQueue.GeneralDelayedQueue;
import com.b0c0.common.domain.vo.GeneralResultVo;

/**
 * @program: springbootdemo
 * @description: 通用延时队列消费
 * @author: lidongsheng
 * @createData: 2020-09-21 15:01
 * @updateAuthor: lidongsheng
 * @updateData: 2020-09-21 15:01
 * @updateContent:
 * @Version: 0.0.8
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 * ************************************************
 * Copyright @ 李东升 2020. All rights reserved
 * ************************************************
 */


/**
 * 开发者实现这个GeneralQueueConsumerable接口来实现重试
 */
public interface GeneralQueueConsumerable {

    /**
     * 开发者要进行执行的具体业务方法，重写run方法即可执行自己的业务逻辑。
     * @param task 具体任务
     * @return 返回false根据重发的策略进行重发执行方法，如果设置了自定义的重发，则会根据重试机制进行重试，true表示执行结束。
     */

    <T>GeneralResultVo<T> run(GeneralDelayedQueue task);
}
