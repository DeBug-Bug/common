package com.b0c0.common.logrecord;


/**
 * @program: common
 * @description:
 * @author: lidongsheng
 * @createData: 2022-08-08 16:03
 * @updateAuthor: lidongsheng
 * @updateData:
 * @updateContent:
 * @Version: 1.0.3
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 */
public class LogRecordBO {

    private Object oldObject;
    private Object newObject;
    private Integer typeCode;
    private Integer moduleCode;
    private Integer moduleClassifyCode;
    private Object bizNoO;
    private Object companyIdO;
    private String fileName;
    private String sortFiedName;

    public Object getOldObject() {
        return oldObject;
    }

    public void setOldObject(Object oldObject) {
        this.oldObject = oldObject;
    }

    public Object getNewObject() {
        return newObject;
    }

    public void setNewObject(Object newObject) {
        this.newObject = newObject;
    }

    public Integer getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(Integer typeCode) {
        this.typeCode = typeCode;
    }

    public Integer getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(Integer moduleCode) {
        this.moduleCode = moduleCode;
    }

    public Integer getModuleClassifyCode() {
        return moduleClassifyCode;
    }

    public void setModuleClassifyCode(Integer moduleClassifyCode) {
        this.moduleClassifyCode = moduleClassifyCode;
    }

    public Object getBizNoO() {
        return bizNoO;
    }

    public void setBizNoO(Object bizNoO) {
        this.bizNoO = bizNoO;
    }

    public Object getCompanyIdO() {
        return companyIdO;
    }

    public void setCompanyIdO(Object companyIdO) {
        this.companyIdO = companyIdO;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSortFiedName() {
        return sortFiedName;
    }

    public void setSortFiedName(String sortFiedName) {
        this.sortFiedName = sortFiedName;
    }
}
