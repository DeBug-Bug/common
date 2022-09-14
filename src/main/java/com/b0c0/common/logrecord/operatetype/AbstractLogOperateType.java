package com.b0c0.common.logrecord.operatetype;


import com.b0c0.common.logrecord.LogOperaTypeEnum;
import com.b0c0.common.logrecord.LogRecordBO;
import com.b0c0.common.logrecord.LogRecordException;
import com.b0c0.common.logrecord.LogRecordResultBO;
import com.b0c0.common.logrecord.anno.LogRecordParamAnno;
import com.b0c0.common.utils.DateUtils;
import com.b0c0.common.utils.SortListUtil;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.b0c0.common.utils.DateUtils.SHORT_DATE_FORMAT;


/**
 * @program: common
 * @description: 根据OperateType进行不同的逻辑抽象类
 * @author: lidongsheng
 * @createData: 2022-08-05 17:14
 * @updateAuthor: lidongsheng
 * @updateData:
 * @updateContent: 根据OperateType进行不同的逻辑抽象类
 * @Version: 1.0.3
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 */
public abstract class AbstractLogOperateType {

    private static final Logger logger = Logger.getLogger(AbstractLogOperateType.class.getName());

    public LogRecordResultBO execute(LogRecordBO logRecordBO) {
        validate(logRecordBO.getOldObject(), logRecordBO.getNewObject());

        Object oldObject = logRecordBO.getOldObject();
        Object newObject = logRecordBO.getNewObject();
        Map<String, List<LogRecordResultBO.ChangeObject>> changeObjectMap = new HashMap<>();

        // 基本类型
        if ((isBaseType(logRecordBO.getOldObject()) && isJavaSelfType(logRecordBO.getOldObject()))
                || (isBaseType(logRecordBO.getNewObject()) && isJavaSelfType(logRecordBO.getNewObject()))) {
            getBaseTypeResultBO(changeObjectMap, logRecordBO);
            return LogRecordResultBO.getSuccessInstance(changeObjectMap);
        }

        // 对象类型
        if ((oldObject != null && !isClassCollection(oldObject.getClass())) ||
                (newObject != null && !isClassCollection(newObject.getClass()))) {
            getObjectLogRecordResultBO(changeObjectMap, logRecordBO, oldObject, newObject);
            return LogRecordResultBO.getSuccessInstance(changeObjectMap);
        }

        // 集合类型处理
        getCollectionLogRecordResultBO(changeObjectMap, logRecordBO, oldObject, newObject);
        return LogRecordResultBO.getSuccessInstance(changeObjectMap);
    }

    private void getBaseTypeResultBO(Map<String, List<LogRecordResultBO.ChangeObject>> changeObjectMap, LogRecordBO logRecordBO) {

        LogRecordResultBO.ChangeObject changeObject = new LogRecordResultBO.ChangeObject();
        changeObject.setFieldOldO(logRecordBO.getOldObject());
        changeObject.setFieldNewO(logRecordBO.getNewObject());
        changeObject.setMoudle(logRecordBO.getModuleCode());
        changeObject.setMoudleClassify(logRecordBO.getModuleClassifyCode());
        changeObject.setType(logRecordBO.getTypeCode());
        String fieldName = logRecordBO.getNewObject() == null ? logRecordBO.getOldObject().getClass().getName() : logRecordBO.getNewObject().getClass().getName();
        changeObject.setFieldName(fieldName);
        if (logRecordBO.getBizNoO() == null || logRecordBO.getCompanyIdO() == null) {
            throw new LogRecordException("基本类型的bizNo和companyId请在 LogRecordAnno 注解上直接设置");
        }
        changeObject.setCompanyId((String) logRecordBO.getCompanyIdO());
        changeObject.setBizNo((String) logRecordBO.getBizNoO());
        changeObjectMap.put((String) logRecordBO.getBizNoO(), Collections.singletonList(changeObject));
    }

    protected void validate(Object oldObject, Object newObject) {
        if (oldObject == null && newObject == null) {
            throw new LogRecordException("oldObject 和 newObject 不能同时为空");
        }
        if (oldObject == null || newObject == null) {
            return;
        }
        Class<?> oldClass = oldObject.getClass();
        if (!oldClass.isInstance(newObject)) {
            throw new LogRecordException("oldExpression 和 newExpression 返回值类型不一致");
        }
    }


    private void getCollectionLogRecordResultBO(Map<String, List<LogRecordResultBO.ChangeObject>> changeObjectMap, LogRecordBO logRecordBO, Object oldObject, Object newObject) {

        List<Object> oldObjectList = null, newObjectList = null;

        if (oldObject != null) {
            oldObjectList = (List<Object>) oldObject;
        }
        if (newObject != null) {
            newObjectList = (List<Object>) newObject;
        }

        if (oldObjectList != null && newObjectList != null && oldObjectList.size() != newObjectList.size()) {
            throw new LogRecordException("list 类型 旧值和新值 list 数量不一致");
        }
        int size = oldObjectList != null ? oldObjectList.size() : newObjectList.size();

        if (oldObjectList != null && newObjectList != null && size > 1 && !StringUtils.hasText(logRecordBO.getSortFiedName())) {
            throw new LogRecordException("list 类型 sortFiedName 不能为空");
        }

        if (oldObjectList != null && newObjectList != null && size > 1) {
            oldObjectList = (List<Object>) SortListUtil.sort(oldObjectList, logRecordBO.getSortFiedName());
            newObjectList = (List<Object>) SortListUtil.sort(newObjectList, logRecordBO.getSortFiedName());
        }
        for (int i = 0; i < size; i++) {
            Object oldObjectByList = oldObjectList != null ? oldObjectList.get(i) : null;
            Object newObjectByList = newObjectList != null ? newObjectList.get(i) : null;
            getObjectLogRecordResultBO(changeObjectMap, logRecordBO, oldObjectByList, newObjectByList);
        }
    }

    private void getObjectLogRecordResultBO(Map<String, List<LogRecordResultBO.ChangeObject>> changeObjectMap, LogRecordBO logRecordBO, Object oldObject, Object newObject) {
        Field[] fields = newObject == null ? oldObject.getClass().getDeclaredFields() : newObject.getClass().getDeclaredFields();
        // 获取需要比对的值
        List<Field> validateFields = Arrays.stream(fields).filter(x -> x.isAnnotationPresent(LogRecordParamAnno.class)).collect(Collectors.toList());
        Optional<Field> bizNoField = validateFields.stream().filter(x -> x.getAnnotation(LogRecordParamAnno.class).bizNoFlag()).findFirst();
        Optional<Field> companyIdField = validateFields.stream().filter(x -> x.getAnnotation(LogRecordParamAnno.class).companyId()).findFirst();
        if (!bizNoField.isPresent()) {
            logger.info("操作日志记录 不存在唯一的业务编码");
            return;
        }
        if (!companyIdField.isPresent()) {
            logger.info("操作日志记录 不存在公司id");
            return;
        }
        String bizNo = "";
        String companyId = "";
        try {
            bizNoField.ifPresent(field -> field.setAccessible(true));
            companyIdField.ifPresent(field -> field.setAccessible(true));
            bizNo = newObject == null ? (String) bizNoField.get().get(oldObject) : (String) bizNoField.get().get(newObject);
            companyId = newObject == null ? (String) companyIdField.get().get(oldObject) : (String) companyIdField.get().get(newObject);
        } catch (Exception ex) {
            logger.severe("操作日志记录 获取bizNo、companyId 异常 " + ex);
            return;
        }

        for (Field x : validateFields) {
            Field oldField = null;
            Field newField = null;
            Object oldFiedValue = null;
            Object newFiedValue = null;
            try {

                if (oldObject != null) {
                    oldField = oldObject.getClass().getDeclaredField(x.getName());
                    oldField.setAccessible(true);
                    oldFiedValue = oldField.get(oldObject);
                }
                if (newObject != null) {
                    newField = newObject.getClass().getDeclaredField(x.getName());
                    newField.setAccessible(true);
                    newFiedValue = newField.get(newObject);
                }

                if (oldFiedValue == null && newFiedValue == null) {
                    continue;
                }

                if ((oldObject != null && oldFiedValue != null && !isBaseType(oldFiedValue)) ||
                        (newObject != null && newFiedValue != null && !isBaseType(newFiedValue))) {
                    logger.warning("操作日志记录 只支持比对基本类型、不支持比对对象类型 字段名 -> " + x.getName());
                    continue;
                }

                if (isNotNeedWriteLog(oldFiedValue, newFiedValue)) {
                    continue;
                }

                // 获取参数注解
                LogRecordParamAnno logRecordParamAnno = x.getAnnotation(LogRecordParamAnno.class);
                List<LogRecordResultBO.ChangeObject> changeObjectList = changeObjectMap.get(bizNo);
                if (changeObjectList == null) {
                    changeObjectList = new ArrayList<>();
                }

                LogRecordResultBO.ChangeObject changeObject = new LogRecordResultBO.ChangeObject();
                changeObject.setFieldName(x.getName());
                changeObject.setFieldDesc(logRecordParamAnno.value());
                changeObject.setMoudle(logRecordBO.getModuleCode());
                changeObject.setType(logRecordBO.getTypeCode());
                int moudleClassifyCode = logRecordParamAnno.moduleClassifyCode();
                if (logRecordBO.getModuleClassifyCode() != null) {
                    moudleClassifyCode = logRecordBO.getModuleClassifyCode();
                }

                changeObject.setMoudleClassify(moudleClassifyCode);
                changeObject.setCompanyId(companyId);
                changeObject.setBizNo(bizNo);
                changeObject.setFieldOldO(oldFiedValue);
                changeObject.setFieldNewO(newFiedValue);
                if (logRecordParamAnno.fieldMappingFlag() && (isIntOrByteType(oldFiedValue) || isIntOrByteType(newFiedValue)) && logRecordParamAnno.fieldMapping().length > 0) {
                    if (oldObject != null && isIntOrByteType(oldFiedValue)) {
                        changeObject.setFieldOldO(logRecordParamAnno.fieldMapping()[(int) oldFiedValue]);
                    }
                    if (newObject != null && isIntOrByteType(newFiedValue)) {
                        changeObject.setFieldNewO(logRecordParamAnno.fieldMapping()[(int) newFiedValue]);
                    }
                }

                if (logRecordParamAnno.fieldMappingFlag() && (isBooleanType(oldFiedValue) || isBooleanType(newFiedValue))) {
                    if (oldObject != null && isBooleanType(oldFiedValue)) {
                        changeObject.setFieldOldO(logRecordParamAnno.fieldMapping().length <= 0 ? (Boolean) oldFiedValue ? "是" : "否"
                                : (Boolean) oldFiedValue ? logRecordParamAnno.fieldMapping()[1] : logRecordParamAnno.fieldMapping()[0]);
                    }
                    if (newObject != null && isBooleanType(newFiedValue)) {
                        changeObject.setFieldNewO(logRecordParamAnno.fieldMapping().length <= 0 ? (Boolean) newFiedValue ? "是" : "否"
                                : (Boolean) newFiedValue ? logRecordParamAnno.fieldMapping()[1] : logRecordParamAnno.fieldMapping()[0]);
                    }
                }

                changeObjectList.add(changeObject);
                changeObjectMap.put(bizNo, changeObjectList);
                // 新增、删除无需记录所有字段
                Integer[] logOperaTypes = new Integer[]{LogOperaTypeEnum.ADD.getCode(), LogOperaTypeEnum.DELETE.getCode()};
                if (Arrays.asList(logOperaTypes).contains(logRecordBO.getTypeCode())) {
                    changeObject.setFieldOldO(null);
                    changeObject.setFieldNewO(null);
                    changeObject.setFieldName(null);
                    changeObject.setFieldDesc(null);
                    break;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 是否需要记录操作日志,比对旧值和新值是否更改
     *
     * @param oldFiedValue
     * @param newFiedValue
     * @return
     */
    private boolean isNotNeedWriteLog(Object oldFiedValue, Object newFiedValue) {

        if ((oldFiedValue instanceof String || newFiedValue instanceof String) && !StringUtils.hasText((String) oldFiedValue) && !StringUtils.hasText((String) newFiedValue)) {
            return true;
        }

        return (oldFiedValue != null && newFiedValue != null && oldOIsEqualNewO(oldFiedValue, newFiedValue));
    }


    public static boolean isClassCollection(Class c) {
        return Collection.class.isAssignableFrom(c);
    }

    protected boolean oldOIsEqualNewO(Object oldObject, Object newObject) {
        if (oldObject instanceof BigDecimal) {
            BigDecimal oldLocalDateTime = (BigDecimal) oldObject;
            BigDecimal newLocalDateTime = (BigDecimal) newObject;
            return oldLocalDateTime.compareTo(newLocalDateTime) == 0;
        }
        if (oldObject instanceof LocalDateTime) {
            LocalDateTime oldLocalDateTime = (LocalDateTime) oldObject;
            LocalDateTime newLocalDateTime = (LocalDateTime) newObject;
            String oldStr = DateUtils.LocalDateTimeStr(oldLocalDateTime, SHORT_DATE_FORMAT);
            String newStr = DateUtils.LocalDateTimeStr(newLocalDateTime, SHORT_DATE_FORMAT);
            return oldStr.equals(newStr);
        }
        return oldObject.equals(newObject);
    }


    /**
     * 是否是基本数据类型
     */
    public final static boolean isBaseType(Object object) {
        if (object == null) {
            return false;
        }
        Class<?> c = object.getClass();
        if (c.isPrimitive()
                || c.isEnum()
                || c.isArray()
                || c.isAnnotation()
                || object instanceof LocalDateTime
                || object instanceof BigDecimal
                || object instanceof String
                || object instanceof Integer
                || object instanceof Double
                || object instanceof Float
                || object instanceof Long
                || object instanceof Boolean
                || object instanceof Byte
                || object instanceof Short) {
            return true;
        } else {
            return false;
        }
    }


    /**
     *
     */
    public final static boolean isIntOrByteType(Object object) {
        if (object == null) {
            return false;
        }
        Class<?> c = object.getClass();
        if (object instanceof Integer
                || object instanceof Byte) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     */
    public final static boolean isBooleanType(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Boolean) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否java自带对象
     */
    public final static boolean isJavaSelfType(Object object) {
        Class<?> c = object.getClass();
        if (c.isPrimitive()
                || (c.getName().startsWith("java"))) {
            return true;
        } else {
            return false;
        }
    }
}
