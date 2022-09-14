package com.b0c0.common.logrecord;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


/**
 * @program: common
 * @description: 操作日志记录上下文变量
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
public class LogRecordContext {

    private static final InheritableThreadLocal<Stack<Map<String, Object>>> variableMapStack = new InheritableThreadLocal<>();


    /**
     * 设置上下文变量
     * @param key
     * @param value
     */
    public static void putVariable(String key, Object value){
        if(variableMapStack.get() == null) {
            variableMapStack.set(new Stack<>());
        }
        Stack<Map<String, Object>> stack = variableMapStack.get();
        if(!stack.isEmpty()){
            Map<String, Object> map = stack.peek();
            map.put(key, value);
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        variableMapStack.get().push(map);
    }


    public static void putEmptySpan(){
        if(variableMapStack.get() == null) {
            variableMapStack.set(new Stack<>());
        }
        Map<String, Object> map = new HashMap<>();
        variableMapStack.get().push(map);
    }

    public static void putVariable(Map<String, Object> map){
        variableMapStack.get().push(map);
    }

    public static Object getVariable(String key){
        return variableMapStack.get().peek().get(key);
    }

    public static Map<String, Object> getVariables(){
        if(variableMapStack.get() != null){
            return variableMapStack.get().peek();
        }
        return null;
    }

    public static void clear(){
        variableMapStack.remove();
    }


}
