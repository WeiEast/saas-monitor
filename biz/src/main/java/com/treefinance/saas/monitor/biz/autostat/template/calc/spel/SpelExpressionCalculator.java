package com.treefinance.saas.monitor.biz.autostat.template.calc.spel;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.monitor.biz.autostat.template.calc.ExpressionCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

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
    public Object calculate(Map<String, Object> dataMap, String expression) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        dataMap.keySet().forEach(key -> context.setVariable(key, dataMap.get(key)));
        Object value = null;
        try {
            context.registerFunction("count", this.getClass().getDeclaredMethod("count", Object.class));
            context.registerFunction("distinct", this.getClass().getDeclaredMethod("distinct", Object.class));
            SpelExpression spelExpression = (SpelExpression) parser.parseExpression(expression);
            spelExpression.setEvaluationContext(context);
            value = spelExpression.getValue();
            logger.info("spel calculate {} : result={}", expression, value);
        } catch (Exception e) {
            logger.error(" calculate expression={} for data={}", expression, JSON.toJSONString(expression), e);
            throw new RuntimeException(e);
        }
        return value;
    }


    public static int count(Object object) {
        return object == null ? 0 : 1;
    }

    public static int distinct(Object object) {
        return object == null ? 0 : 1;
    }

//
//    public static void main(String[] args) {
//        String json = "{\"appId\":\"QATestabcdefghQA\",\"bizType\":3,\"completeTime\":1516959226000,\"monitorType\":\"task\",\"status\":1,\"stepCode\":\"\",\"taskId\":141595882901499904,\"uniqueId\":\"test\"}";
//        Map<String, Object> map = JSON.parseObject(json);
//        SpelExpressionCalculator calculator = new SpelExpressionCalculator();
//        System.out.println(calculator.calculate(map, "#distinct(#uniqueId)"));
//    }

}
