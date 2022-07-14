package com.b0c0.common.log;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.TaskManagementConfigUtils;

/**
 * @program: common
 * @description:
 * @author: 李东升
 * @create: 2022-07-14 10:45
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class LogRecordConfiguration implements BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Bean(name = "logAnnoAspect")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LogAnnoAspect logAnnoAspect() {
        return new LogAnnoAspect(beanFactory);
    }

}
