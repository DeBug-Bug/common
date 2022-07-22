package com.b0c0.common.logrecord;

import java.lang.annotation.*;

/**
 * @program: springBootDemo
 * @description:
 * @author: 李东升
 * @create: 2022-07-13 17:22
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogRecordParamAnno {

    /**
     * 字段名称，记录拼接操作变更字符串的时候时候会拼接这个字段名称
     * @return
     */
    String value();
}
