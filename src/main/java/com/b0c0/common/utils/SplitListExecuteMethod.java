package com.b0c0.common.utils;

import java.util.List;

/**
 * @program: common
 * @description: 将list分割成指定长度的字串并执行业务逻辑 具体执行的方法
 * @author: lidongsheng
 * @createData: 2022-06-30 11:12
 * @updateAuthor: lidongsheng
 * @updateData: 2022-06-30 11:12
 * @updateContent:
 * @Version: 1.0.3
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 * ************************************************
 * Copyright @ 李东升 2020. All rights reserved
 * ************************************************
 */
public interface SplitListExecuteMethod<T> {

    /**
     * 具体执行的方法
     * @param subList 指定长度的字串list
     * @param param 额外参数
     */
    void doSomething(List<T> subList, Object... param);
}
