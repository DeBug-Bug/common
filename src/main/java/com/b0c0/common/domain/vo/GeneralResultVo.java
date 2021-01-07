package com.b0c0.common.domain.vo;

/**
 * @program: common
 * @description: 延时队列执行结果
 * @author: lidongsheng
 * @createData: 2020-10-24 13:47
 * @updateAuthor: lidongsheng
 * @updateData: 2020-10-24 13:47
 * @updateContent: 延时队列执行结果
 * @Version: 0.0.8
 * @email: lidongshenglife@163.com
 * @blog: www.b0c0.com
 * ************************************************
 * Copyright @ 李东升 2020. All rights reserved
 * ************************************************
 */

public class GeneralResultVo<T> {

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

    public GeneralResultVo(boolean success, String reslutCode, String reslutMessage, T reslutData) {
        this.success = success;
        this.reslutCode = reslutCode;
        this.reslutMessage = reslutMessage;
        this.reslutData = reslutData;
    }

    public static<T> GeneralResultVo<T> fail() {
        return new GeneralResultVo<T>(false, GeneralResultCodeEnum.EXECU_ERROR.getCode(), "执行失败", null);
    }

    public static<T> GeneralResultVo<T> fail(String reslutCode, String reslutMessage) {
        return new GeneralResultVo<T>(false, reslutCode, reslutMessage, null);
    }

    public static<T> GeneralResultVo<T> success() {
        return new GeneralResultVo<T>(true, GeneralResultCodeEnum.SUCCESS.getCode(), "执行成功", null);
    }

    public static<T> GeneralResultVo<T> success(T reslutData) {
        return new GeneralResultVo<T>(true, GeneralResultCodeEnum.SUCCESS.getCode(), "执行成功", reslutData);
    }

    public static<T> GeneralResultVo<T> success(String reslutCode, String reslutMessage, T reslutData) {
        return new GeneralResultVo<T>(true, reslutCode, reslutMessage, reslutData);
    }
}
