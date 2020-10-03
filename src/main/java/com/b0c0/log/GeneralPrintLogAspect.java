package com.b0c0.log;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * @program: springBootDemo
 * @description: AOP拦截切面类
 * @author: lidongsheng
 * @createData:
 * @updateAuthor: lidongsheng
 * @updateData:
 * @updateContent: AOP拦截切面类
 * @Version: 1.0
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 */
@Aspect
@Component
public class GeneralPrintLogAspect {


    private static final Logger logger = LoggerFactory.getLogger(GeneralPrintLogAspect.class);

    /**
     * 注解是拦截定义切入点，切入点为com.example.aop下的所有函数
     */
    @Pointcut(value = "@annotation(generalPrintLogAOP)", argNames = "generalPrintLogAOP")
    public void annotationPoinCut(GeneralPrintLogAOP generalPrintLogAOP) {
    }

    /**
     * 环绕通知 在目标方法完成前后做增强处理，也是最重要的通知类型，像事务,日志等都是环绕通知。
     *
     * @param joinPoint
     * @return
     */
    @Around("annotationPoinCut(generalPrintLogAOP)")
    public Object invoke(ProceedingJoinPoint joinPoint, GeneralPrintLogAOP generalPrintLogAOP) {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        String uuid = UUID.nameUUIDFromBytes(String.valueOf(threadGroup.hashCode()).getBytes()).toString();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        //得到执行方法的类名
        String className = methodSignature.getDeclaringType().getSimpleName();
        //得到执行方法的方法名
        String methodName = methodSignature.getMethod().getName();
        //.通过这获取到方法的所有参数名称的字符串数组
        String[] parameterNames = methodSignature.getParameterNames();
        Parameter[] parameters;
        parameters = methodSignature.getMethod().getParameters();
        if (parameterNames == null) {
            parameterNames = new String[parameters != null ? parameters.length : 0];
        }
        List<Integer> onjectParamSubList = new ArrayList<>();
        for (int i = 0; i < parameterNames.length; ++i) {
            String typeName = parameters[i].getType().getSimpleName();
            if (parameterNames == null) {
                parameterNames[i] = "type:" + typeName;
            }
        }
        //得到参数值
        Object[] args = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder("通用日志打印AOP uuid -> " + uuid + "  方法解释 -> " + generalPrintLogAOP.value() + " 类名 -> " + className + " 方法名 -> " + methodName
                + " 请求参数 -> ");
        for (int i = 0; i < parameterNames.length; i++) {
            sb.append(" " + parameterNames[i] + " -> ");
            sb.append(JSON.toJSONString(args[i]) + ",");
        }
        logger.info(sb.toString());
        long beginExecuteTime = System.currentTimeMillis();
        Object obj = null;
        try {
            //调用执行目标方法并得到执行方法的返回值
            obj = joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.error("通用日志打印AOP uuid -> " + uuid + " 类名 -> {} 方法名 -> {} 执行出异常,异常信息：", className, methodName, throwable.toString());
            throwable.printStackTrace();
        }
        long endExecuteTime = System.currentTimeMillis();
        logger.info("通用日志打印AOP uuid -> " + uuid + " 方法解释 -> {} 类名 -> {} 方法名 -> {} 执行完成，执行结果 -> {}，执行时间 -> {}", generalPrintLogAOP.value(), className, methodName, JSON.toJSONString(obj), (endExecuteTime - beginExecuteTime) + "ms");
        return obj;
    }


}
