package com.b0c0.common.log;

import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.SchedulingConfiguration;

import java.lang.annotation.*;

/**
 * @program: common
 * @description:
 * @author: 李东升
 * @create: 2022-07-14 10:44
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(LogRecordConfiguration.class)
@Documented
public @interface EnableLogRecord {

}
