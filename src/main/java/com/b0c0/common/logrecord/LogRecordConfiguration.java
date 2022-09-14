package com.b0c0.common.logrecord;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @program: common
 * @description:
 * @author: lidongsheng
 * @createData:2022-07-14 10:45
 * @updateAuthor: lidongsheng
 * @updateData:
 * @updateContent:
 * @Version: 1.0.3
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 */

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class LogRecordConfiguration{


    @Bean(name = "logAnnoAspect")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LogRecordAnnoAspect logAnnoAspect() {
        return new LogRecordAnnoAspect();
    }




}
