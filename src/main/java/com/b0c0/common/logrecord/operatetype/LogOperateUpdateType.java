package com.b0c0.common.logrecord.operatetype;

import com.b0c0.common.logrecord.LogRecordConst;
import org.springframework.stereotype.Service;

import static com.b0c0.common.logrecord.LogRecordConst.OPERATE_TYPE_UPDATE;

/**
 * @program: common
 * @description: 更新
 * @author: lidongsheng
 * @createData: 2022-08-05 19:27
 * @updateAuthor: lidongsheng
 * @updateData:
 * @updateContent: 更新
 * @Version: 1.0.3
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 */
@Service(LogRecordConst.LOG_OPERATE_TYPE + OPERATE_TYPE_UPDATE)
public class LogOperateUpdateType extends AbstractLogOperateType{
    @Override
    protected boolean isSaveAllField() {
        return true;
    }
}
