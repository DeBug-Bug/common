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
 * @Version: 1.0.0
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 * ************************************************
 * Copyright @ 李东升 2020. All rights reserved
 * ************************************************
 */


/**
 * 开发者实现这个GeneralQueueConsumerable接口并指定 执行方法的返回值和请求参数数据类型.
 * @param <T> 返回值泛型
 * @param <V> 开发者自定义的需要传递的业务数据泛型
 */
public interface GeneralQueueConsumerable<T,V> {

    /**
     * 开发者要进行执行的具体业务方法，重写run方法即可执行自己的业务逻辑。
     * @param task 具体任务 V：开发者自定义的需要传递的业务数据泛型
     * @return 返回false根据重发的策略进行重发执行方法，如果设置了自定义的重发，则会根据重试机制进行重试，true表示执行结束。
     */
    GeneralResultVo<T> run(GeneralDelayedQueue<V> task);
}
