package com.b0c0.common.domain.vo;
/**
 * @program: mavenJar
 * @description: 
 * @author: lidongsheng
 * @createData: 2020/12/31 10:54
 * @updateAuthor: lidongsheng
 * @updateData:
 * @updateContent: 
 * @Version: 1.0.0
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 */
public enum GeneralResultCodeEnum {

    SUCCESS("20000","执行成功"),
    //程序错误44开头
    EXECU_ERROR("44000", "执行失败"),
    TIME_OUT("44001", "执行超时"),
    //开发者方错误 45开头
    PARAM_ERROR("45001", "参数错误"),
    TASK_EXIST("45002", "任务不存在");
    private String code;
    private String desc;

    GeneralResultCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
