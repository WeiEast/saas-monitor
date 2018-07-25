package com.treefinance.saas.monitor.biz.alarm.expression.spel;

import com.treefinance.saas.monitor.biz.alarm.expression.ExpressionParser;
import com.treefinance.saas.monitor.biz.alarm.expression.spel.func.SpelFunction;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Created by yh-treefinance on 2018/7/23.
 */
@Component("spelExpressionParser")
public class SpelExpressionParser implements ExpressionParser {

    /**
     * spel 解析器
     */
    private org.springframework.expression.ExpressionParser parser = new org.springframework.expression.spel.standard.SpelExpressionParser();


    @Override
    public Object parse(String expression, Map<String, Object> context) {

        // 1. 数据准备
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        context.keySet().forEach(key -> evaluationContext.setVariable(key, context.get(key)));

        // 2.注册函数
        for (Method method : SpelFunction.class.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            evaluationContext.registerFunction(method.getName(), method);
        }

        // 3.计算数据
        SpelExpression spelExpression = (SpelExpression) parser.parseExpression(expression);
        spelExpression.setEvaluationContext(evaluationContext);
        return spelExpression.getValue();
    }
}
