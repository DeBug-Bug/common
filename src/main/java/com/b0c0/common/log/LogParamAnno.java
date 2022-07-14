package com.b0c0.common.log;

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
public @interface LogParamAnno{

    String value();
}
