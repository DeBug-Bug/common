package com.b0c0.common.log;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @program: common
 * @description: 存方法的变量
 * @author: 李东升
 * @create: 2022-07-07 15:20
 */
public class LogRecordContext {

    private static final InheritableThreadLocal<Stack<Map<String, Object>>> variableMapStack = new InheritableThreadLocal<>();


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
        variableMapStack.get().pop();
    }


}
