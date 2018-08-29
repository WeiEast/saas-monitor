package com.treefinance.saas.monitor.biz.alarm.expression.thymeleaf;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.alarm.expression.Analysis;
import com.treefinance.saas.monitor.biz.alarm.expression.ExpressionParser;
import com.treefinance.saas.monitor.biz.alarm.expression.spel.func.SpelFunction;
import com.treefinance.saas.monitor.biz.alarm.model.EMessageType;
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
@Analysis(EMessageType.HTML)
@Component("thymeleafExpressionParser")
public class ThymeleafExpressionParser implements ExpressionParser {
    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    @Override
    public Object parse(String expression, Map<String, Object> context) {
        // html 使用thymeleaf解析
        Context thymeleafCtx = new Context();
        thymeleafCtx.setVariables(context);
        String html = springTemplateEngine.process(expression, thymeleafCtx);
        logger.info("parse expression by thymeleaf : result={} ,   expression={}, context={}", html, expression, JSON.toJSONString(context));
        return html;
    }
}
