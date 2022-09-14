package com.b0c0.common.logrecord;

/**
 * @program: common
 * @description: 操作日志类型success类型的返回值
 * @author: lidongsheng
 * @createData: 2022-09-14 15:32
 * @updateAuthor: lidongsheng
 * @updateData:
 * @updateContent: 操作日志类型success类型的返回值
 * @Version: 1.0.3
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 */
public interface ILogRecordValidateSuccessService {

    /**
     * 业务自定义方法返回error
     * @param proceed
     */
    boolean validateSuccess(Object proceed);
}
