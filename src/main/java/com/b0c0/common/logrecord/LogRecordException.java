package com.b0c0.common.logrecord;


/**
 * @program: common
 * @description: 操作日志异常
 * @author: lidongsheng
 * @createData:
 * @updateAuthor: lidongsheng
 * @updateData:
 * @updateContent:
 * @Version: 1.0.3
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 */
public class LogRecordException extends RuntimeException{
    private static final long serialVersionUID = -1260892180669737478L;
    private String errMsg;
    private Object errObj;

    public LogRecordException(String message) {
        super(message);
        this.errMsg = message;
    }

    public LogRecordException() {
    }

    public Object getErrObj() {
        return errObj;
    }

    public void setErrObj(Object errObj) {
        this.errObj = errObj;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
