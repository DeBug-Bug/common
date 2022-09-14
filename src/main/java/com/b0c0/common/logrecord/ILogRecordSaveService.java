package com.b0c0.common.logrecord;

/**
 * @program: common
 * @description: 操作日志记录日志
 * @author: lidongsheng
 * @createData: 2022-08-03 19:10
 * @updateAuthor: lidongsheng
 * @updateData:
 * @updateContent: 操作日志记录日志
 * @Version: 1.0.3
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 */
public interface ILogRecordSaveService {

    /**
     * 保存log
     * @param logRecordAnno
     */
    void saveLog(LogRecordResultBO logRecordAnno);
}
