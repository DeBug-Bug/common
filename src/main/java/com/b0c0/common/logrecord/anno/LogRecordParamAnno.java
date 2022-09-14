package com.b0c0.common.logrecord.anno;

import java.lang.annotation.*;

/**
 * @program: sap-ecc-fico-bpc-ossims-core
 * @description:
 * @author: 李东升
 * @create: 2022-07-13 17:22
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogRecordParamAnno {

    /**
     * 字段自定义描述
     * @return
     */
    String value();

    /**
     * 是否为操作日志绑定的业务对象标识,一个实体类中必须有一个
     * @return
     */
    boolean bizNoFlag() default false;

    /**
     * 该字段是否为公司id字段
     * @return
     */
    boolean companyId() default false;

    /**
     * 操作模块细项分类
     * @return
     */
    int moduleClassifyCode() default -1;

    /**
     * 字段值是否需要映射 例如字段为：状态0、1 需要映射为 草稿、生效
     * @return
     */
    boolean fieldMappingFlag() default false;

    /**
     * 字段值映射 {"草稿","生效"} 下标对应字段值
     * 如果是Boolean类型的，{"false对应的映射","true 对应的映射"}, 如果不填默认为{false:否、true:是}
     * @return
     */
    String[] fieldMapping() default {};
}
