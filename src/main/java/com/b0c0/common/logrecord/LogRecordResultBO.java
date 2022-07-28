package com.b0c0.common.logrecord;

import java.util.List;

/**
 * @program: common
 * @description:
 * @author: 李东升
 * @create: 2022-07-28 17:53
 */

public class LogRecordResultBO {

    /**
     * 记录结果
     */
    private Boolean result;

    /**
     * 异常原因
     */
    private String errMessage;

    /**
     * 日志内容
     */
    private List<StringBuilder> logContentList;

    /**
     * 日志注解
     */
    private LogRecordAnno logRecordAnno;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public List<StringBuilder> getLogContentList() {
        return logContentList;
    }

    public void setLogContentList(List<StringBuilder> logContentList) {
        this.logContentList = logContentList;
    }

    public LogRecordAnno getLogRecordAnno() {
        return logRecordAnno;
    }

    public void setLogRecordAnno(LogRecordAnno logRecordAnno) {
        this.logRecordAnno = logRecordAnno;
    }

    public LogRecordResultBO(){

    }

    public LogRecordResultBO(Boolean result, String errMessage){
        this.result = result;
        this.errMessage = errMessage;
    }

    public static LogRecordResultBO getErrorInstance(String errMessage){
        return new LogRecordResultBO(false, errMessage);
    }

    public static LogRecordResultBO getSuccessInstance(List<StringBuilder> logContentList, LogRecordAnno logRecordAnno){
        LogRecordResultBO logRecordResultBO = new LogRecordResultBO();
        logRecordResultBO.setResult(true);
        logRecordResultBO.setLogContentList(logContentList);
        logRecordResultBO.setLogRecordAnno(logRecordAnno);
        return logRecordResultBO;
    }

}
