package com.treefinance.saas.monitor.biz.autostat.template.calc.spel;

import com.treefinance.saas.monitor.biz.autostat.template.calc.ExpressionCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * 数据计算器
 * Created by yh-treefinance on 2018/1/24.
 */
@Component
public class SpelExpressionCalculator implements ExpressionCalculator {
    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * spel 解析器
     */
    private ExpressionParser parser = new SpelExpressionParser();

    @Override
    public Object calculate(Object data, String expression) {
        StandardEvaluationContext context = new StandardEvaluationContext(data);
        Expression spelExpression = parser.parseExpression(expression);
        Object value = spelExpression.getValue(context);
        logger.info("spel calculate {} : result={}", expression, value);
        return value;
    }
}
