package com.b0c0.common.logrecord.operatetype;

import com.b0c0.common.logrecord.LogRecordBO;
import com.b0c0.common.logrecord.LogRecordConst;
import com.b0c0.common.logrecord.LogRecordException;
import com.b0c0.common.logrecord.LogRecordResultBO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @program: common
 * @description: 导入
 * @author: lidongsheng
 * @createData: 2022-08-08 15:25
 * @updateAuthor: lidongsheng
 * @updateData:
 * @updateContent: 导入
 * @Version: 1.0.3
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 */
@Service(LogRecordConst.LOG_OPERATE_TYPE + LogRecordConst.OPERATE_TYPE_IMPORT_FILE)
public class LogOperateImportType extends AbstractLogOperateType {

    public LogRecordResultBO execute(LogRecordBO logRecordBO) {
        Map<String, List<LogRecordResultBO.ChangeObject>> changeObjectMap = new HashMap<>();
        LogRecordResultBO.ChangeObject changeObject = new LogRecordResultBO.ChangeObject();
        changeObject.setMoudle(logRecordBO.getModuleCode());
        changeObject.setMoudleClassify(logRecordBO.getModuleClassifyCode());
        changeObject.setType(logRecordBO.getTypeCode());
        if (logRecordBO.getBizNoO() == null || logRecordBO.getCompanyIdO() == null) {
            throw new LogRecordException("基本类型的bizNo和companyId请在 LogRecordAnno 注解上直接设置");
        }
        changeObject.setFieldName(logRecordBO.getFileName());
        changeObject.setFieldDesc(logRecordBO.getFileName());
        changeObject.setCompanyId((String) logRecordBO.getCompanyIdO());
        changeObject.setBizNo((String) logRecordBO.getBizNoO());
        changeObjectMap.put((String) logRecordBO.getBizNoO(), Collections.singletonList(changeObject));
        return LogRecordResultBO.getSuccessInstance(changeObjectMap);

    }

}
