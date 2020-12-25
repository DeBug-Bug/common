package com.b0c0.common.delayedQueue.base;


import com.b0c0.common.delayedQueue.GeneralDelayedQueue;
import com.b0c0.common.utils.GeneralResult;

/**
 * @program: springbootdemo
 * @description: 通用延时队列消费
 * @author: lidongsheng
 * @createData: 2020-09-21 15:01
 * @updateAuthor: lidongsheng
 * @updateData: 2020-09-21 15:01
 * @updateContent:
 * @Version: 1.0.0
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 * ************************************************
 * Copyright @ 李东升 2020. All rights reserved
 * ************************************************
 */

public interface GeneralQueueConsumerable {

    /**
     * 开发者要进行执行的具体业务方法，开发者实现这个GeneralQueueConsumerable接口重写里面的run方法即可执行自己的业务逻辑。
     * @param task
     * @return false表示执行业务失败，如果设置了失败重试，则会根据重试机制进行重试，true表示执行成功
     */
    <T>GeneralResult<T> run(GeneralDelayedQueue task);
}
