package com.b0c0.common.log;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: common
 * @description:
 * @author: 李东升
 * @create: 2022-07-08 14:53
 */
@Aspect
public class LogAnnoAspect{
    //定义解析的模板
    private static final TemplateParserContext PARSER_CONTEXT = new TemplateParserContext();
    //定义解析器
    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    //定义评估的上下文对象
    private final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();

    public LogAnnoAspect(BeanFactory beanFactory){
        this.beanFactory = beanFactory;
        this.evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
    }
    private BeanFactory beanFactory;

//
//    @Override
//    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
//        this.beanFactory = beanFactory;
//        //填充evaluationContext对象的`BeanFactoryResolver`。
//        this.evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
//    }

    @Pointcut(value = "@annotation(logAnno)", argNames = "logAnno")
    public void annotationPoinCut(LogAnno logAnno) {
    }

    @Around(value = "annotationPoinCut(logAnno);")
    public Object around(ProceedingJoinPoint joinPoint, LogAnno logAnno) throws Throwable {
        Signature signature = joinPoint.getSignature();
        //获取参数名称
        Map<String, Object> argsMap = getNameAndValue(joinPoint);

        // 把请求入参的参数放到 RootObject 中
        if (argsMap.size() > 0) {
            for (Map.Entry<String, Object> entry : argsMap.entrySet()) {
                evaluationContext.setVariable(entry.getKey(), entry.getValue());
            }
        }
        MethodSignature methodSignature = (MethodSignature) signature;
        Object target = joinPoint.getTarget();
        //获取到当前执行的方法
        Method method = target.getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        //执行方法
        Object proceed = joinPoint.proceed();

        // 把LogRecordContext 中的变量都放到 RootObject 中
        Map<String, Object> variables = LogRecordContext.getVariables();
        if (variables != null && variables.size() > 0) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                evaluationContext.setVariable(entry.getKey(), entry.getValue());
            }
        }
        // 解析表达式
        Object oldObject = PARSER.parseExpression(logAnno.oldExpression(), PARSER_CONTEXT)
                .getValue(this.evaluationContext, evaluationContext.getRootObject());

        Object newObject = PARSER.parseExpression(logAnno.newExpression(), PARSER_CONTEXT)
                .getValue(this.evaluationContext, evaluationContext.getRootObject());
        if(oldObject == null){
            return proceed;
        }
        Class<?> oldClass = oldObject.getClass();
        StringBuilder logContent = new StringBuilder();
        if(!oldClass.isInstance(newObject)){
            System.out.println("oldExpression 和 newExpression 返回值类型不一致");
        }
        if(logAnno.returnBaseType() && oldObject.equals(newObject)){
            logContent.append(logAnno.message()).append("旧值为：").append(oldObject).append("新值为:").append(newObject);
        }else {
            Field[] fields = oldClass.getDeclaredFields();
            logContent.append(logAnno.message());
            JSONObject oldJsonObject = JSONObject.parseObject(JSONObject.toJSONString(oldObject));
            JSONObject newJsonObject = JSONObject.parseObject(JSONObject.toJSONString(newObject));
            Arrays.stream(fields).forEach(x ->{
                if (x.isAnnotationPresent(LogParamAnno.class)) {
                    // 获取注解值
                    String name = x.getAnnotation(LogParamAnno.class).value();
                    logContent.append("[").append(name).append("]")
                            .append("旧值为：").append(oldJsonObject.get(x.getName())).append(" 新值为:").append(newJsonObject.get(x.getName())).append(";");
                }
            });
        }
        System.out.println(logContent);
        return proceed;
    }

    /**
     * 获取参数Map集合
     * @param joinPoint
     * @return
     */
    private Map<String, Object> getNameAndValue(ProceedingJoinPoint joinPoint) {
        Map<String, Object> param = new HashMap<>();
        Object[] paramValues = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature)joinPoint.getSignature()).getParameterNames();
        for (int i = 0; i < paramNames.length; i++) {
            param.put(paramNames[i], paramValues[i]);
        }
        return param;
    }
}
