package com.b0c0.common.logrecord;

import java.util.Arrays;
import java.util.Optional;

/**
 * @program: common
 * @description: 操作类型枚举
 * @author: lidongsheng
 * @createData: 2022-08-03 19:22
 * @updateAuthor: lidongsheng
 * @updateData:
 * @updateContent: 操作类型枚举
 * @Version: 1.0.3
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 */
public enum LogOperaTypeEnum {


    /**
     *
     */
    NONE(-1, ""),

    ADD(1, "新增"),
    UPDATE(2, "更新"),
    DELETE(3, "删除"),
    UPLOAD(4, "上传文件"),
    IMPORT_FILE(5, "导入"),
    EXPORT_FILE(6, "导出"),
    ;

    private final int code;
    private final String desc;

    LogOperaTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static LogOperaTypeEnum getByCode(int code) {
        Optional<LogOperaTypeEnum> optional = Arrays.stream(values()).filter(x-> code == x.getCode()).findFirst();
        return optional.orElse(null);
    }

}
