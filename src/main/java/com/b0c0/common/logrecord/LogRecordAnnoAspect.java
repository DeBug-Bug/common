package com.b0c0.common.logrecord;

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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: common
 * @description:
 * @author: 李东升
 * @create: 2022-07-08 14:53
 */
@Aspect
public class LogRecordAnnoAspect implements BeanFactoryAware {
    //定义解析的模板
    private static final TemplateParserContext PARSER_CONTEXT = new TemplateParserContext();
    //定义解析器
    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    //定义评估的上下文对象
    private final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
    //获取到Spring容器的beanFactory对象
    private BeanFactory beanFactory;

    private ILogRecordSaveService logRecordSaveService;

    public LogRecordAnnoAspect(ILogRecordSaveService logRecordSaveService){
        this.logRecordSaveService = logRecordSaveService;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        //填充evaluationContext对象的`BeanFactoryResolver`。
        this.evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
    }

    @Pointcut(value = "@annotation(logRecordAnno)", argNames = "logRecordAnno")
    public void annotationPoinCut(LogRecordAnno logRecordAnno) {
    }

    /**
     * 比对操作步骤
     * 1. 获取{@LogAnno}注解方法上的入参并放到SPEL解析的上下文变量中
     * 2. 如果oldExpressionExecBeforeFlag = true，则在业务方法执行之前执行解析执行旧值表达式，获取到旧值
     * 3. 执行业务方法
     * 4. 把业务方法设置到LogRecordContext的变量放到SPEL上下文变量中
     * 5. 判断是否执行2步骤
     * 6. 判断oldExpression 和 newExpression 返回值类型不一致直接报错
     * 7. 判断returnBaseType
     *      7.1 returnBaseType=true ， 则说明旧值和新值都是基本类型，直接比对。
     *      7.2 returnBaseType=false， 获取旧值对象上面所有有@LogParamAnno注解字段、比对这些字段值，如果不相等则记录这些字段
     * 8. 拼接字段生成操作变更记录String字符串
     * 9. 储存操作日志记录（业务可自定义实现储存方式）
     * @param joinPoint
     * @param logRecordAnno
     * @return
     * @throws Throwable
     */
    @Around(value = "annotationPoinCut(logRecordAnno);")
    public Object around(ProceedingJoinPoint joinPoint, LogRecordAnno logRecordAnno) throws Throwable {
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

        // 旧值表达式执行返回的对象
        Object oldObject = null;

        // 是否在业务方法执行之前执行解析执行旧值表达式
        if(logRecordAnno.oldExpressionExecBeforeFlag()){
            oldObject = PARSER.parseExpression(logRecordAnno.oldExpression(), PARSER_CONTEXT)
                    .getValue(this.evaluationContext, evaluationContext.getRootObject());
        }
        //执行方法
        Object proceed = joinPoint.proceed();

        // 把LogRecordContext 中的变量都放到 RootObject 中
        Map<String, Object> variables = LogRecordContext.getVariables();
        if (variables != null && variables.size() > 0) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                evaluationContext.setVariable(entry.getKey(), entry.getValue());
            }
        }

        if(!logRecordAnno.oldExpressionExecBeforeFlag()) {
            // 解析表达式
            oldObject = PARSER.parseExpression(logRecordAnno.oldExpression(), PARSER_CONTEXT)
                    .getValue(this.evaluationContext, evaluationContext.getRootObject());
        }

        // 新值表达式执行返回的对象
        Object newObject = PARSER.parseExpression(logRecordAnno.newExpression(), PARSER_CONTEXT)
                .getValue(this.evaluationContext, evaluationContext.getRootObject());
        if (oldObject == null) {
            return proceed;
        }
        Class<?> oldClass = oldObject.getClass();
        List<StringBuilder> logContentList = new ArrayList<>();
        if (!oldClass.isInstance(newObject)) {
            System.out.println("oldExpression 和 newExpression 返回值类型不一致");
        }
        // 如果 logAnno返回的是基础类型，不是对象（int、byte之类的），那个就直接比对拼接就可以
        if (logRecordAnno.returnBaseType() && oldObject.equals(newObject)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(logRecordAnno.message()).append("旧值为：").append(oldObject).append("新值为:").append(newObject);
            logContentList.add(stringBuilder);
            return proceed;
        }

        // 比对对象类型的
        Field[] fields = oldClass.getDeclaredFields();
        JSONObject oldJsonObject = JSONObject.parseObject(JSONObject.toJSONString(oldObject));
        JSONObject newJsonObject = JSONObject.parseObject(JSONObject.toJSONString(newObject));
        // 获取需要比对的值
        List<Field> validateFields = Arrays.stream(fields).filter(x -> x.isAnnotationPresent(LogRecordParamAnno.class)
                && oldJsonObject.get(x.getName()) != null && newJsonObject.get(x.getName()) != null).collect(Collectors.toList());
        validateFields.forEach(x -> {
            StringBuilder stringBuilder = new StringBuilder();
            // 获取参数注解
            LogRecordParamAnno logRecordParamAnno = x.getAnnotation(LogRecordParamAnno.class);
            stringBuilder.append("[").append(logRecordParamAnno.value()).append("(").append(x.getName()).append(")").append("]")
                    .append("旧值为：").append(oldJsonObject.get(x.getName())).append(" 新值为:").append(newJsonObject.get(x.getName())).append(";");
            logContentList.add(stringBuilder);
        });
        logRecordSaveService.saveLog(logContentList, logRecordAnno);
        return proceed;
    }

    /**
     * 获取参数Map集合
     *
     * @param joinPoint
     * @return
     */
    private Map<String, Object> getNameAndValue(ProceedingJoinPoint joinPoint) {
        Map<String, Object> param = new HashMap<>();
        Object[] paramValues = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        for (int i = 0; i < paramNames.length; i++) {
            param.put(paramNames[i], paramValues[i]);
        }
        return param;
    }
}

