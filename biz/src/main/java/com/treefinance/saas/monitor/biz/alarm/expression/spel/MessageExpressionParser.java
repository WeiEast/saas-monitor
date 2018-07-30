package com.treefinance.saas.monitor.biz.alarm.expression.spel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.alarm.expression.ExpressionParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/7/26.
 */
@Component
public class MessageExpressionParser implements ExpressionParser {

    /**
     * spel 表达式
     */
    @Autowired
    private SpelExpressionParser spelExpressionParser;

    @Override
    public Object parse(String expression, Map<String, Object> context) {
        // 解析表达式
        Pattern pattern = Pattern.compile("(#\\S{1,}\\(.+\\)\\s?)|(#\\S{1,}\\s?)");
        List<String> normalparts = pattern.splitAsStream(expression).collect(Collectors.toList());
        List<String> expressions = Lists.newArrayList();
        Matcher expressionMatcher = pattern.matcher(expression);
        while (expressionMatcher.find()) {
            String _expression = expressionMatcher.group();
            expressions.add(_expression);
        }

        // 拼接动态sql
        StringBuffer messageBf = new StringBuffer();
        int size = normalparts.size();
        for (int i = 0; i < size; i++) {
            String sqlpart = normalparts.get(i);
            messageBf.append(sqlpart);
            if (i < expressions.size()) {
                String _expression = expressions.get(i);
                Object result = spelExpressionParser.parse(_expression, context);
                if (result != null) {
                    messageBf.append(result.toString());
                } else {
                    messageBf.append(" ");
                }
            }
        }
        return messageBf.toString();
    }
}
