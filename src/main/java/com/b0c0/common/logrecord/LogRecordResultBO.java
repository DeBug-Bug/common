package com.b0c0.common.logrecord;

import java.util.List;
import java.util.Map;


/**
 * @program: common
 * @description: 操作日志最终结果类
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

public class LogRecordResultBO {

    /**
     * 记录结果
     */
    private Boolean result;

    /**
     * 异常原因
     */
    private String errMessage;


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

    public Map<String, List<ChangeObject>> getChangeObjectMap() {
        return changeObjectMap;
    }

    public void setChangeObjectMap(Map<String, List<ChangeObject>> changeObjectMap) {
        this.changeObjectMap = changeObjectMap;
    }

    /**
     * 更改字段 key为bizNoFlag为true的唯一字段
     */
    private Map<String, List<ChangeObject>> changeObjectMap;




    public LogRecordResultBO(){

    }

    public LogRecordResultBO(Boolean result, String errMessage){
        this.result = result;
        this.errMessage = errMessage;
    }

    public static LogRecordResultBO getErrorInstance(String errMessage){
        return new LogRecordResultBO(false, errMessage);
    }

    public static LogRecordResultBO getSuccessInstance(Map<String, List<ChangeObject>> changeObjectMap){
        LogRecordResultBO logRecordResultBO = new LogRecordResultBO();
        logRecordResultBO.setResult(true);
        logRecordResultBO.setChangeObjectMap(changeObjectMap);
        return logRecordResultBO;
    }

    public static class ChangeObject{

        /**
         * 字段名称
         */
        private String fieldName;

        /**
         * 字段描述
         */
        private String fieldDesc;

        /**
         * 操作类型
         */
        private Integer type;

        /**
         * 操作模块
         */
        private Integer moudle;

        /**
         * 模块细项
         */
        private Integer moudleClassify;

        /**
         * 操作前旧值
         */
        private Object fieldOldO;
        /**
         * 操作前新值
         */
        private Object fieldNewO;

        /**
         * 业务唯一标识id
         */
        private String bizNo;

        /**
         * 公司id
         */
        private String companyId;


        public ChangeObject(){

        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldDesc() {
            return fieldDesc;
        }

        public void setFieldDesc(String fieldDesc) {
            this.fieldDesc = fieldDesc;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Integer getMoudle() {
            return moudle;
        }

        public void setMoudle(Integer moudle) {
            this.moudle = moudle;
        }

        public Integer getMoudleClassify() {
            return moudleClassify;
        }

        public void setMoudleClassify(Integer moudleClassify) {
            this.moudleClassify = moudleClassify;
        }

        public Object getFieldOldO() {
            return fieldOldO;
        }

        public void setFieldOldO(Object fieldOldO) {
            this.fieldOldO = fieldOldO;
        }

        public Object getFieldNewO() {
            return fieldNewO;
        }

        public void setFieldNewO(Object fieldNewO) {
            this.fieldNewO = fieldNewO;
        }

        public String getBizNo() {
            return bizNo;
        }

        public void setBizNo(String bizNo) {
            this.bizNo = bizNo;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }
    }

}
