package com.b0c0.common.logrecord;

import com.b0c0.common.logrecord.anno.LogRecordAnno;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
/**
 * @program: common
 * @description: 操作日志aop拦截类
 * @author: lidongsheng
 * @createData: 2022-08-08 14:53
 * @updateAuthor: lidongsheng
 * @updateData:
 * @updateContent: 操作日志aop拦截类
 * @Version: 1.0.3
 * @email: lidongshenglife@163.com
 * @blog: https://www.b0c0.com
 * @csdn: https://blog.csdn.net/LDSWAN0
 */
@Aspect
public class LogRecordAnnoAspect implements BeanFactoryAware {

    private static final Logger logger = Logger.getLogger(LogRecordAnnoAspect.class.getName());

    //定义解析的模板
    private static final TemplateParserContext PARSER_CONTEXT = new TemplateParserContext();
    //定义解析器
    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    //定义评估的上下文对象
    private final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
    //获取到Spring容器的beanFactory对象
    private BeanFactory beanFactory;

    @Resource
    private ILogRecordSaveService logRecordSaveService;

    @Resource
    private LogRecordExecuteHelper logRecordExecuteHelper;

    @Resource
    private ILogRecordValidateSuccessService validateSuccessService;


    public LogRecordAnnoAspect() {
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        //填充evaluationContext对象的`BeanFactoryResolver`。
        this.evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
    }

    @Pointcut(value = "execution(* *(..)) && @annotation(logRecordAnno)", argNames = "logRecordAnno")
    public void annotationPoinCut(LogRecordAnno logRecordAnno) {
    }

    /**
     * @param joinPoint
     * @param logRecordAnno
     * @return
     * @throws Throwable
     */
    @Around(value = "annotationPoinCut(logRecordAnno);")
    public Object around(ProceedingJoinPoint joinPoint, LogRecordAnno logRecordAnno) throws Throwable {

        // 旧值、新值表达式执行返回的对象
        Object oldObject = null;
        Object newObject = null;

        // 前置操作： 放入入参到上下文变量、获取旧值
        before(joinPoint, logRecordAnno, null);
        Object proceed = null;
        try {
            // 执行业务被拦截方法
            proceed = joinPoint.proceed();
            if(!validateSuccessService.validateSuccess(proceed)){
                LogRecordContext.clear();
                return proceed;
            }
        } catch (Exception ex) {
            LogRecordContext.clear();
            throw ex;
        }

        try {
            long startTime = System.currentTimeMillis();
            // 记录操作日志
            saveOperateLog(logRecordAnno, oldObject, newObject);
            // 执行耗时
            logger.info("操作日志 耗时ms : " + (System.currentTimeMillis() - startTime));
        } catch (LogRecordException logRecordException) {
            logger.severe("记录操作日志异常: " + logRecordException.getErrMsg());
        } catch (Exception ex) {
            logger.severe("记录操作日志异常" + ex);
        } finally {
            LogRecordContext.clear();
        }
        return proceed;
    }

    private void saveOperateLog(LogRecordAnno logRecordAnno, Object oldObject, Object newObject) {
        // 把LogRecordContext 中的变量都放到 RootObject 中
        Map<String, Object> variables = LogRecordContext.getVariables();
        if (variables != null && variables.size() > 0) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                evaluationContext.setVariable(entry.getKey(), entry.getValue());
            }
        }
        LogRecordBO logRecordBO = new LogRecordBO();
        logRecordBO.setOldObject(oldObject);
        logRecordBO.setNewObject(newObject);

        logRecordBO.setModuleClassifyCode(logRecordAnno.moduleClassifyCode());
        // 解析自定义表达式并赋值
        parseCustomExpression(logRecordAnno, logRecordBO);

        logRecordBO.setModuleCode(logRecordAnno.moduleCode());
        logRecordBO.setTypeCode(logRecordAnno.typeCode());
        logRecordBO.setSortFiedName(logRecordAnno.sortFiledName());
        // 根据不同的操作类型比对转换为最终存储用的操作日志对象
        LogRecordResultBO logRecordResultBO = logRecordExecuteHelper.getLogRecordResultBO(logRecordBO);
        logRecordSaveService.saveLog(logRecordResultBO);
    }

    /**
     * 解析自定义表达式
     *
     * @param logRecordAnno
     * @param logRecordBO
     */
    private void parseCustomExpression(LogRecordAnno logRecordAnno, LogRecordBO logRecordBO) {
        Object newObject;
        Object oldObject;
        if (StringUtils.hasText(logRecordAnno.oldExpression()) && !logRecordAnno.oldExpressionExecBeforeFlag()) {
            // 解析表达式
            oldObject = PARSER.parseExpression(logRecordAnno.oldExpression(), PARSER_CONTEXT)
                    .getValue(this.evaluationContext, evaluationContext.getRootObject());
            logRecordBO.setOldObject(oldObject);
        }

        if (StringUtils.hasText(logRecordAnno.newExpression())) {
            // 新值表达式执行返回的对象
            newObject = PARSER.parseExpression(logRecordAnno.newExpression(), PARSER_CONTEXT)
                    .getValue(this.evaluationContext, evaluationContext.getRootObject());
            logRecordBO.setNewObject(newObject);
        }

        if (StringUtils.hasText(logRecordAnno.bizNoExpression())) {
            // bizNo表达式执行返回的对象
            Object bizNoO = PARSER.parseExpression(logRecordAnno.bizNoExpression(), PARSER_CONTEXT)
                    .getValue(this.evaluationContext, evaluationContext.getRootObject());
            if (!isClassCollection(bizNoO) && !isBaseType(bizNoO)) {
                throw new LogRecordException("bizNo 不是基础或者集合类型");
            }
            logRecordBO.setBizNoO(bizNoO);
        }

        if (StringUtils.hasText(logRecordAnno.companyIdExpression())) {
            // 公司id表达式执行返回的对象
            Object companyIdO = PARSER.parseExpression(logRecordAnno.companyIdExpression(), PARSER_CONTEXT)
                    .getValue(this.evaluationContext, evaluationContext.getRootObject());
            if (!isClassCollection(companyIdO) && !isBaseType(companyIdO)) {
                throw new LogRecordException("companyId 不是基础或者集合类型");
            }
            logRecordBO.setCompanyIdO(companyIdO);
        }

        if (StringUtils.hasText(logRecordAnno.fileNameExpression())) {
            // 文件名表达式执行返回的对象
            Object object = PARSER.parseExpression(logRecordAnno.fileNameExpression(), PARSER_CONTEXT)
                    .getValue(this.evaluationContext, evaluationContext.getRootObject());
            if (!isBaseType(object)) {
                throw new LogRecordException("fileName 不是基本类型");
            }
            String fileName = String.valueOf(object);
            logRecordBO.setFileName(fileName);
        }

        if (StringUtils.hasText(logRecordAnno.moduleClassifyExpression())) {
            // 文件名表达式执行返回的对象
            Object object = PARSER.parseExpression(logRecordAnno.moduleClassifyExpression(), PARSER_CONTEXT)
                    .getValue(this.evaluationContext, evaluationContext.getRootObject());
            if (!isBaseType(object)) {
                throw new LogRecordException("操作模块细项 不是基本类型");
            }
            Integer moduleClassifyCode = Integer.valueOf(String.valueOf(object));
            logRecordBO.setModuleClassifyCode(moduleClassifyCode);
        }
    }

    private Object before(ProceedingJoinPoint joinPoint, LogRecordAnno logRecordAnno, Object oldObject) {
        //获取参数名称
        Map<String, Object> argsMap = getNameAndValue(joinPoint);
        // 把请求入参的参数放到 RootObject 中
        if (argsMap.size() > 0) {
            for (Map.Entry<String, Object> entry : argsMap.entrySet()) {
                evaluationContext.setVariable(entry.getKey(), entry.getValue());
            }
        }
        // 是否在业务方法执行之前执行解析执行旧值表达式
        if (StringUtils.hasText(logRecordAnno.oldExpression()) && logRecordAnno.oldExpressionExecBeforeFlag()) {
            oldObject = PARSER.parseExpression(logRecordAnno.oldExpression(), PARSER_CONTEXT)
                    .getValue(this.evaluationContext, evaluationContext.getRootObject());
        }
        return oldObject;
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


    /**
     * 是否是基本数据类型
     */
    public final static boolean isBaseType(Object object) {
        if (object == null) {
            return false;
        }
        Class<?> c = object.getClass();
        if (c.isPrimitive()
                || object instanceof String
                || object instanceof Integer
                || object instanceof Double
                || object instanceof Float
                || object instanceof Long
                || object instanceof Boolean
                || object instanceof Byte
                || object instanceof Short) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isClassCollection(Object c) {
        if (c == null) {
            return false;
        }
        return Collection.class.isAssignableFrom(c.getClass());
    }
}

