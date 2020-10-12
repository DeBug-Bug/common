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
 * @Version: 1.0
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
}
