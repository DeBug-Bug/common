package com.b0c0.common.log;

import java.lang.annotation.*;

/**
 * @program: common
 * @description:
 * @author: 李东升
 * @create: 2022-07-08 14:52
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnno {

    String message() default "修改了信息: ";

    /**
     * 旧值的表达式 oldExpression和newExpression表达式返回值类型必须一致
     * @return
     */
    String oldExpression() default "";

    /**
     * 新值的表达式 oldExpression和newExpression表达式返回值类型必须一致
     * @return
     */
    String newExpression() default "";

    /**
     * 表达式返回值是否为基本类型
     * @return
     */
    boolean returnBaseType();

    /**
     * 操作日志绑定的业务对象标识
     * @return
     */
    String bizNo() default "";

    /**
     * 操作日志种类 增删改查
     * @return
     */
    String category() default "";

    /**
     * 操作日志类型
     * @return
     */
    int type() default 0;
}
