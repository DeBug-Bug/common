package com.b0c0.common.delayedQueue.base;

import com.b0c0.common.delayedQueue.GeneralDelayedQueue;

/**
 * @program: common
 * @description: 重试时间的type接口，自定义的重试时间实现此接口即可
 * @author: lidongsheng
 * @createData: 2020-09-25 19:07
 * @updateAuthor: lidongsheng
 * @updateData: 2020-09-25 19:07
 * @updateContent: 重试时间的type接口
 * @Version: 0.0.8
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 * ************************************************
 * Copyright @ 李东升 2020. All rights reserved
 * ************************************************
 */

public interface RetryTimeTypeable {
    /**
     * 返回延时时间
     * 开发者实现这个此接口重写里面的getTime方法即可根据具体需要进行定义重试机制延时时间。
     * @param task
     * @return 返回此次的延时时间，单位和GeneralDelayedQueue构造方法中设置的时间单位一致（默认为秒）。
     */
    long getTime(GeneralDelayedQueue task);
}
