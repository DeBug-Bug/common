package com.b0c0.common.utils;

/**
 * @program: common
 * @description: 延时队列执行结果
 * @author: lidongsheng
 * @createData: 2020-10-24 13:47
 * @updateAuthor: lidongsheng
 * @updateData: 2020-10-24 13:47
 * @updateContent: 延时队列执行结果
 * @Version: 1.0.0
 * @email: lidongshenglife@163.com
 * @blog: www.b0c0.com
 * ************************************************
 * Copyright @ 李东升 2020. All rights reserved
 * ************************************************
 */

public class GeneralResult<T> {

    private boolean success;

    private String reslutCode;

    private String reslutMessage;

    private T reslutData;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReslutCode() {
        return reslutCode;
    }

    public void setReslutCode(String reslutCode) {
        this.reslutCode = reslutCode;
    }

    public String getReslutMessage() {
        return reslutMessage;
    }

    public void setReslutMessage(String reslutMessage) {
        this.reslutMessage = reslutMessage;
    }

    public T getReslutData() {
        return reslutData;
    }

    public void setReslutData(T reslutData) {
        this.reslutData = reslutData;
    }

    public GeneralResult(boolean success, String reslutCode, String reslutMessage, T reslutData) {
        this.success = success;
        this.reslutCode = reslutCode;
        this.reslutMessage = reslutMessage;
        this.reslutData = reslutData;
    }

    public static<T> GeneralResult<T> fail(String reslutCode, String reslutMessage) {
        return new GeneralResult<T>(false, reslutCode, reslutMessage, null);
    }

    public static<T> GeneralResult<T> success() {
        return new GeneralResult<T>(false, "200", "执行成功", null);
    }

    public static<T> GeneralResult<T> success(T reslutData) {
        return new GeneralResult<T>(false, "200", "执行成功", reslutData);
    }

    public static<T> GeneralResult<T> success(String reslutCode, String reslutMessage, T reslutData) {
        return new GeneralResult<T>(false, reslutCode, reslutMessage, reslutData);
    }
}
