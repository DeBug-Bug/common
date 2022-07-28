package com.b0c0.common.logrecord;

import java.util.List;

/**
 * @program: common
 * @description: 操作日志记录日志
 * @author: 李东升
 * @create: 2022-07-22 19:10
 */
public interface ILogRecordSaveService {

    /**
     * 保存log
     * @param logRecordAnno
     */
    void saveLog(LogRecordResultBO logRecordAnno);
}
