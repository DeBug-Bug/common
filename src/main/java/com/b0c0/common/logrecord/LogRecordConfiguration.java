package com.b0c0.common.logrecord;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import javax.annotation.Resource;

/**
 * @program: common
 * @description:
 * @author: 李东升
 * @create: 2022-07-14 10:45
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class LogRecordConfiguration{


    @Resource
    private ILogRecordSaveService logRecordSaveService;

    @Bean(name = "logAnnoAspect")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LogRecordAnnoAspect logAnnoAspect() {
        return new LogRecordAnnoAspect(logRecordSaveService);
    }




}
