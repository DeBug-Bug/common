package com.b0c0.common.logrecord;

import com.b0c0.common.logrecord.operatetype.AbstractLogOperateType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @program: common
 * @description:
 * @author: lidongsheng
 * @createData: 2022-08-05 17:46
 * @updateAuthor: lidongsheng
 * @updateData:
 * @updateContent:
 * @Version: 1.0.3
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 */
@Component
public class LogRecordExecuteHelper {

    @Resource
    private Map<String, AbstractLogOperateType> logOperateTypeMap;


    public LogRecordResultBO getLogRecordResultBO(LogRecordBO logRecordBO){
        Object oldObject = logRecordBO.getOldObject();
        Object newObject = logRecordBO.getNewObject();
        // 前置通用校验
        validate(oldObject, newObject);
        // 没有设置类型的，进行默认策略识别是何种操作类型（仅能识别增、删、改）
        setDefLogOperaTypeEnum(logRecordBO);
        // 获取对应操作类型的执行类
        AbstractLogOperateType abstractLogOperateType = logOperateTypeMap.get(LogRecordConst.LOG_OPERATE_TYPE + logRecordBO.getTypeCode());
        if(abstractLogOperateType == null){
            throw new LogRecordException("操作日志记录 未映射到合适的操作类型");
        }
        return abstractLogOperateType.execute(logRecordBO);
    }


    public boolean validate(Object oldObject, Object newObject){
        if(oldObject == null && newObject == null){
            throw new LogRecordException("操作日志记录 旧值和新值不能同时为空");
        }
        return true;
    }

    /**
     * 获取默认的操作类型、通过旧值和新值的
     * @return
     */
    public void setDefLogOperaTypeEnum(LogRecordBO logRecordBO){
        Object oldObject = logRecordBO.getOldObject();
        Object newObject = logRecordBO.getNewObject();
        Integer typeCode = logRecordBO.getTypeCode();
        if(LogOperaTypeEnum.NONE.getCode() == typeCode){
            return;
        }
        if(oldObject != null && newObject != null){
            logRecordBO.setTypeCode(LogOperaTypeEnum.UPDATE.getCode());
            return;
        }
        if(oldObject == null && newObject != null){
            logRecordBO.setTypeCode(LogOperaTypeEnum.ADD.getCode());
            return;
        }
        if(oldObject != null){
            logRecordBO.setTypeCode(LogOperaTypeEnum.DELETE.getCode());
            return;
        }
        throw new LogRecordException("操作日志记录 获取默认操作类型异常");

    }
}
