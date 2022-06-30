package com.b0c0.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: common
 * @description: 将list分割成指定长度的字串并执行业务逻辑的通用工具类
 * @author: lidongsheng
 * @createData: 2022-06-30 11:12
 * @updateAuthor: lidongsheng
 * @updateData: 2022-06-30 11:12
 * @updateContent:
 * @Version: 1.0.3
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 * ************************************************
 * Copyright @ 李东升 2020. All rights reserved
 * ************************************************
 */
public class SplitListExecuteUtil {

     public static void execute(SplitListExecuteMethod function, List list, int limit, Object... param){
         int dataSize = list.size();
         int num = dataSize % limit == 0 ? dataSize / limit : (dataSize / limit) + 1;
         for (int i = 0; i < num; i++) {
             int begin = i * limit;
             int end = (i == (num - 1)) ? list.size() : begin + limit;
             List subList = list.subList(begin, end);
             function.doSomething(subList, param);
         }
    }


    public static void main(String[] args) {
        List<Integer> sourceList = new ArrayList<>();
        for (int i = 0; i < 29; i++) {
            sourceList.add(i);
        }
        SplitListExecuteUtil.execute((SplitListExecuteMethod<Integer>) (subList, param) -> {
            System.out.println(subList);
        }, sourceList, 5);
    }

}
