package com.treefinance.saas.monitor.biz.alarm.expression.spel;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.monitor.biz.alarm.expression.Analysis;
import com.treefinance.saas.monitor.biz.alarm.expression.ExpressionParser;
import com.treefinance.saas.monitor.biz.alarm.expression.spel.func.SpelFunction;
import com.treefinance.saas.monitor.biz.alarm.model.EAnalysisType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.BeanExpressionContextAccessor;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Created by yh-treefinance on 2018/7/23.
 */
@Analysis(EAnalysisType.TEXT)
@Component("spelExpressionParser")
public class SpelExpressionParser implements ExpressionParser {

    /**
     * spel 解析器
     */
    private org.springframework.expression.ExpressionParser parser = new org.springframework.expression.spel.standard.SpelExpressionParser();

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public Object parse(String expression, Map<String, Object> context) {

        Object result = null;
        try {
            // 1. 数据准备
            StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
            evaluationContext.addPropertyAccessor(new MapAccessor());
            evaluationContext.addPropertyAccessor(new BeanExpressionContextAccessor());
            evaluationContext.addPropertyAccessor(new BeanFactoryAccessor());

            evaluationContext.setRootObject(context);
            // 2.注册函数
            for (Method method : SpelFunction.class.getDeclaredMethods()) {
                if (!Modifier.isStatic(method.getModifiers())) {
                    continue;
                }
                evaluationContext.registerFunction(method.getName(), method);
            }

            // 3.计算数据
            Expression spelExpression = parser.parseExpression(expression, new TemplateParserContext());
            result = spelExpression.getValue(evaluationContext);
            return result;
        } finally {
            logger.info("spel expression parser : expression={},result={}, context={}", JSON.toJSONString(expression), JSON.toJSONString(result), JSON.toJSONString(context));
        }
    }
}
