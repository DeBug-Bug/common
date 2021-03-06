package com.b0c0.common.log;

import java.lang.annotation.*;

/**
 * @program:
 * @description: 使用AOP注解拦截
 * @author:
 * @createData:
 * @updateAuthor:
 * @updateData:
 * @updateContent: 使用AOP注解拦截
 * @Version: 0.0.8
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 */

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
/**
 * 使用务必再项目启动类上加上
 * @Import({GeneralPrintLogAspect.class})
 */
public @interface GeneralPrintLogAOP {
    /**
     * 说明信息
     * @return
     */
    String value() default "";

    /**
     * 设置辞职控制日志的输出字符串个数
     * @return
     */
    int maxLogLength() default 99999;
}
