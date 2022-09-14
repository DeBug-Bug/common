package com.b0c0.common.logrecord.anno;

import com.b0c0.common.logrecord.LogRecordConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @program: sap-ecc-fico-bpc-ossims-core
 * @description:
 * @author: 李东升
 * @create: 2022-08-03 10:44
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(LogRecordConfiguration.class)
@Documented
public @interface EnableLogRecord {

}
