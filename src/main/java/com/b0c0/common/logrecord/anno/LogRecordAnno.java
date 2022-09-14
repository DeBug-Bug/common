package com.b0c0.common.logrecord.anno;


import java.lang.annotation.*;

/**
 * @program: sap-ecc-fico-bpc-ossims-core
 * @description: 操作注解，在需要记录操作日志的方法上面添加
 * @author: 李东升
 * @create: 2022-08-05 16:52
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogRecordAnno {

    /**
     * 旧值的表达式 oldExpression和newExpression表达式返回值类型必须一致
     * @return
     */
    String oldExpression() default "";

    /**
     * 旧值的表达式 oldExpression 执行解析是否在业务方法执行之前
     * @return
     */
    boolean oldExpressionExecBeforeFlag() default false;

    /**
     * 新值的表达式 oldExpression和newExpression表达式返回值类型必须一致
     * @return
     */
    String newExpression() default "";

    /**
     * 唯一业务标识表达式， 只限于是 旧值的表达式 或者 新值的表达式 的值是基本类型的话或者是list类型的基本类型
     * @return
     */
    String bizNoExpression() default "";


    /**
     * 公司id表达式， 只限于是 旧值的表达式 或者 新值的表达式 的值是基本类型的话传此项或者是list类型的基本类型
     * @return
     */
    String companyIdExpression() default "";

    /**
     * 操作模块细项分类枚举 如果 LogRecordParamAnno 字段注解中标识了 moduleClassify  则以 LogRecordParamAnno 字段标识的为准
     * @return
     */
    int moduleClassifyCode() default -1;

    /**
     * 操作模块细项分类表达式
     * @return
     */
    String moduleClassifyExpression() default "";

    /**
     * 操作日志所属模块 具体业务自定义
     * @return
     */
    int moduleCode();

    /**
     * 操作日志类型 增 删 改 查 等，具体业务自定义
     * @return
     */
    int typeCode() default -1;

    /**
     * 如果是导入、导出、文件格式的，在此处放入文件名称表达式
     * @return
     */
    String fileNameExpression() default "";

    /**
     * 集合类型排序字段
     */
    String sortFiledName() default "";
}
