package com.b0c0.common.logrecord;

/**
 * @program: common
 * @description:
 * @author: lidongsheng
 * @createData:  2022-08-05 19:29
 * @updateAuthor: lidongsheng
 * @updateData:
 * @updateContent:
 * @Version: 1.0.3
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 */
public interface LogRecordConst {

    String LOG_OPERATE_TYPE = "LOG_OPERATE_ADD_TYPE_";


    /**
     *     UPDATE(2, "更新"),
     *     DELETE(3, "删除"),
     *     upload(4, "上传文件"),
     *     import_file(5, "导入"),
     *     export_file(6, "导出"),
     */
    String OPERATE_TYPE_ADD = "1";
    String OPERATE_TYPE_UPDATE = "2";
    String OPERATE_TYPE_DELETE = "3";
    String OPERATE_TYPE_UPLOAD = "4";
    String OPERATE_TYPE_IMPORT_FILE = "5";
    String OPERATE_TYPE_EXPORT_FILE = "6";

}
