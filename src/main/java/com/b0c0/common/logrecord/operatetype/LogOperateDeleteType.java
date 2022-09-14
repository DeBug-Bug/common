package com.b0c0.common.logrecord.operatetype;

import com.b0c0.common.logrecord.LogRecordBO;
import com.b0c0.common.logrecord.LogRecordConst;
import com.b0c0.common.logrecord.LogRecordException;
import com.b0c0.common.logrecord.LogRecordResultBO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: common
 * @description: 删除
 * @author: lidongsheng
 * @createData: 2022-08-08 15:23
 * @updateAuthor: lidongsheng
 * @updateData:
 * @updateContent: 删除
 * @Version: 1.0.3
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 */
@Service(LogRecordConst.LOG_OPERATE_TYPE + LogRecordConst.OPERATE_TYPE_DELETE)
public class LogOperateDeleteType extends AbstractLogOperateType{

    public LogRecordResultBO execute(LogRecordBO logRecordBO) {
        Object oldObject = logRecordBO.getOldObject();
        Object newObject = logRecordBO.getNewObject();
        Map<String, List<LogRecordResultBO.ChangeObject>> changeObjectMap = new HashMap<>();
        // 如果不是集合类型的，直接交由父类处理
        if ((oldObject != null && !isClassCollection(oldObject.getClass())) ||
                (newObject != null && !isClassCollection(newObject.getClass()))) {
            return super.execute(logRecordBO);
        }
        List<Object> oldObjectList = null, newObjectList = null;
        if (oldObject == null) {
            throw new LogRecordException("删除类型 旧值不允许为空");
        }
        oldObjectList = (List<Object>) oldObject;
        if (newObject != null) {
            newObjectList = (List<Object>) newObject;
        }
        if (oldObjectList != null && newObjectList != null && oldObjectList.size() != newObjectList.size()) {
            throw new LogRecordException("list 类型 旧值和新值 list 数量不一致");
        }

        // 如果集合中的对象不是基本类型的，直接交由父类处理
        if ((oldObjectList != null && !isBaseType(oldObjectList.get(0))) ||
                (newObjectList != null && !isBaseType(newObjectList.get(0)))) {
            return super.execute(logRecordBO);
        }

        List<String> bizNoList = (List<String>) logRecordBO.getBizNoO();
        List<String> companyIdList = (List<String>) logRecordBO.getCompanyIdO();

        if (CollectionUtils.isEmpty(bizNoList) || CollectionUtils.isEmpty(companyIdList)) {
            throw new LogRecordException("bizNo和companyId请在 LogRecordAnno 注解上直接设置,并且值为list类型");
        }

        int size = oldObjectList != null ? oldObjectList.size() : newObjectList.size();
        if(size != bizNoList.size() || size != companyIdList.size()){
            throw new LogRecordException("bizNo和companyId集合 size 不一致");
        }
        for (int i = 0; i < size; i++) {
            Object oldObjectByList = oldObjectList != null ? oldObjectList.get(i) : null;
            getBaseTypeResultBO(changeObjectMap, logRecordBO, oldObjectByList, bizNoList.get(i), companyIdList.get(i));
        }
        return LogRecordResultBO.getSuccessInstance(changeObjectMap);
    }


    private void getBaseTypeResultBO(Map<String, List<LogRecordResultBO.ChangeObject>> changeObjectMap, LogRecordBO logRecordBO, Object oldObject, String bizNo, String companyId) {

        LogRecordResultBO.ChangeObject changeObject = new LogRecordResultBO.ChangeObject();
        changeObject.setMoudle(logRecordBO.getModuleCode());
        changeObject.setMoudleClassify(logRecordBO.getModuleClassifyCode());
        changeObject.setType(logRecordBO.getTypeCode());
        String fieldName = logRecordBO.getNewObject() == null ? logRecordBO.getOldObject().getClass().getName() : logRecordBO.getNewObject().getClass().getName();
        changeObject.setFieldName(fieldName);
        if (logRecordBO.getBizNoO() == null || logRecordBO.getCompanyIdO() == null) {
            throw new LogRecordException("基本类型的bizNo和companyId请在 LogRecordAnno 注解上直接设置");
        }
        changeObject.setCompanyId(companyId);
        changeObject.setBizNo(bizNo);
        List<LogRecordResultBO.ChangeObject> changeObjectList = changeObjectMap.get(bizNo);
        if (changeObjectList == null) {
            changeObjectList = new ArrayList<>();
        }
        changeObjectList.add(changeObject);
        changeObjectMap.put(bizNo, changeObjectList);
    }



}
