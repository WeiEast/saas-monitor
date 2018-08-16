package com.treefinance.saas.monitor.biz.alarm.expression.spel;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.alarm.expression.ExpressionParser;
import com.treefinance.saas.monitor.biz.alarm.expression.spel.func.SpelFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;


import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/7/26.
 */
@Component("messageExpressionParser")
public class MessageExpressionParser implements ExpressionParser {
    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * spel 表达式
     */
    @Autowired
    private SpelExpressionParser spelExpressionParser;

    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    @Override
    public Object parse(String expression, Map<String, Object> context) {
        // 是否为html
        boolean isHtml = expression.matches("<(S*?)[^>]*>.*?|<.*? />");
        // html 使用thymeleaf解析
        if (isHtml) {
            Context thymeleafCtx = new Context();
            thymeleafCtx.setVariables(context);
            String html = springTemplateEngine.process(expression, thymeleafCtx);
            logger.info("parse expression by thymeleaf : result={} ,   expression={}, context={}", html, expression, JSON.toJSONString(context));
            return html;
        } else {
            Object result = spelExpressionParser.parse(expression, context);
            logger.info("parse expression by spel : result={} ,  expression={}, context={}", JSON.toJSONString(result), expression, JSON.toJSONString(context));
            return result;
        }
        // 解析表达式
//        Pattern pattern = Pattern.compile(SpelFunction.SPEL_PATTERN);
//        List<String> normalparts = pattern.splitAsStream(expression).collect(Collectors.toList());
//        List<String> expressions = Lists.newArrayList();
//        Matcher expressionMatcher = pattern.matcher(expression);
//        while (expressionMatcher.find()) {
//            String _expression = expressionMatcher.group();
//            expressions.add(_expression);
//        }
//
//        // 拼接动态sql
//        StringBuffer messageBf = new StringBuffer();
//        int size = normalparts.size();
//        for (int i = 0; i < size; i++) {
//            String sqlpart = normalparts.get(i);
//            messageBf.append(sqlpart);
//            if (i < expressions.size()) {
//                String _expression = expressions.get(i);
//                Object result = spelExpressionParser.parse(_expression, context);
//                if (result != null) {
//                    messageBf.append(result.toString());
//                } else {
//                    messageBf.append(" ");
//                }
//            }
//        }
    }
}
