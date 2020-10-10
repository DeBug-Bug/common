package com.b0c0.common.delayedQueue.base;


import com.b0c0.common.delayedQueue.GeneralDelayedQueue;

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

    boolean run(GeneralDelayedQueue task);
}
