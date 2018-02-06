package com.treefinance.saas.monitor.biz.autostat.template.calc.spel;

import com.alibaba.fastjson.JSON;
import com.datatrees.toolkits.util.other.DataUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.autostat.model.AsConstants;
import com.treefinance.saas.monitor.biz.autostat.template.calc.ExpressionCalculator;
import com.treefinance.saas.monitor.biz.autostat.utils.CronUtils;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 数据计算器
 * Created by yh-treefinance on 2018/1/24.
 */
@Component
public class SpelExpressionCalculator implements ExpressionCalculator {
    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(SpelExpressionCalculator.class);

    /**
     * context
     */
    private static ThreadLocal<Map<String, Object>> context = new ThreadLocal<>();

    /**
     * spel 解析器
     */
    private ExpressionParser parser = new SpelExpressionParser();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void initContext(String key, Object value) {
        Map<String, Object> contextMap = context.get();
        if (contextMap == null) {
            contextMap = Maps.newHashMap();
            context.set(contextMap);
        }
        contextMap.put(key, value);
    }

    /**
     * 移除
     *
     * @param key
     */
    public void destroyContext(String key) {
        Map<String, Object> contextMap = context.get();
        if (contextMap != null) {
            contextMap.remove(key);
        }
    }

    @Override
    public Object calculate(Map<String, Object> dataMap, String expression) {
        initContext(AsConstants.EXPRESSION, expression);
        initContext(AsConstants.REDIS, redisTemplate);
        StandardEvaluationContext context = new StandardEvaluationContext();
        dataMap.keySet().forEach(key -> context.setVariable(key, dataMap.get(key)));
        Object value = null;
        try {
            initContext(AsConstants.DATA, dataMap);

            context.registerFunction("count", this.getClass().getDeclaredMethod("count", Object.class));
            context.registerFunction("distinct", this.getClass().getDeclaredMethod("distinct", Object.class));
            context.registerFunction("exists", this.getClass().getDeclaredMethod("exists", Object.class));
            context.registerFunction("day", this.getClass().getDeclaredMethod("day", Long.class));

            SpelExpression spelExpression = (SpelExpression) parser.parseExpression(expression);
            spelExpression.setEvaluationContext(context);
            value = spelExpression.getValue();
            logger.info("spel calculate {} : result={}", expression, value);
        } catch (Exception e) {
            logger.error(" calculate expression={} for data={}", expression, JSON.toJSONString(expression), e);
            throw new RuntimeException(e);
        } finally {
            destroyContext(AsConstants.EXPRESSION);
            destroyContext(AsConstants.DATA);
        }
        return value;
    }


    /**
     * count 计数
     *
     * @param object
     * @return
     */
    public static int count(Object object) {
        return object == null ? 0 : 1;
    }

    /**
     * distinct 去重
     *
     * @param object
     * @return
     */
    public static int distinct(Object object) {
        if (object == null) {
            return 0;
        }
        StatTemplate statTemplate = (StatTemplate) context.get().get(AsConstants.STAT_TEMPLATE);
        String expression = context.get().get(AsConstants.EXPRESSION).toString();
        long timeInterval = CronUtils.getTimeInterval(statTemplate.getStatCron());

        String dataTimeStr = (String) context.get().get(AsConstants.DATA_TIME);
        String redisKey = Joiner.on(":").useForNull("null").join(AsConstants.REDIS_PREFIX,
                statTemplate.getTemplateCode(), "distinct", expression, dataTimeStr);

        StringRedisTemplate redisTemplate = (StringRedisTemplate) context.get().get(AsConstants.REDIS);
        String value = object.toString();
        if (redisTemplate.boundSetOps(redisKey).isMember(value)) {
            return 0;
        }
        redisTemplate.boundSetOps(redisKey).add(value);
        redisTemplate.boundSetOps(redisKey).expire(2 * timeInterval, TimeUnit.MILLISECONDS);
        return 1;
    }

    /**
     * 是否存在
     *
     * @param object
     * @return
     */
    public static boolean exists(Object object) {
        if (object == null) {
            return false;
        }
        StatTemplate statTemplate = (StatTemplate) context.get().get(AsConstants.STAT_TEMPLATE);
        String expression = context.get().get(AsConstants.EXPRESSION).toString();
        long timeInterval = CronUtils.getTimeInterval(statTemplate.getStatCron());

        String dataTimeStr = (String) context.get().get(AsConstants.DATA_TIME);
        String redisKey = Joiner.on(":").useForNull("null").join(AsConstants.REDIS_PREFIX,
                statTemplate.getTemplateCode(), "exists", expression, dataTimeStr);

        StringRedisTemplate redisTemplate = (StringRedisTemplate) context.get().get(AsConstants.REDIS);
        String value = object.toString();
        if (redisTemplate.boundSetOps(redisKey).isMember(value)) {
            return true;
        }
        redisTemplate.boundSetOps(redisKey).add(value);
        redisTemplate.boundSetOps(redisKey).expire(2 * timeInterval, TimeUnit.MILLISECONDS);
        return false;
    }


    /**
     * 获取日
     *
     * @param times
     * @return
     * @throws ParseException
     */
    public static Long day(Long times) throws ParseException {
        Date date = new Date(times);
        String day = DateFormatUtils.format(date, "yyyy-MM-dd");
        date = DateUtils.parseDate(day, "yyyy-MM-dd");
        return date.getTime();
    }


    public static void main(String[] args) {
        System.out.println(DateFormatUtils.format(1517403542000L, "yyyy-MM-dd HH:mm:ss"));
        String json = "{\"appId\":\"QATestabcdefghQA\",\"bizType\":3,\"completeTime\":1516959226000,\"monitorType\":\"task\",\"status\":1,\"stepCode\":\"\",\"taskId\":141595882901499904,\"uniqueId\":\"test\"}";
        Map<String, Object> map = JSON.parseObject(json);
        SpelExpressionCalculator calculator = new SpelExpressionCalculator();
        System.out.println(calculator.calculate(map, "#distinct(#uniqueId)"));

        //
        json = "{\"accountNo\":\"1$zcR5gBUG1c83qEjS4spSxJAAAAwA\",\"appId\":\"QATestabcdefghQA\",\"bizType\":2,\"createTime\":1517403542000,\"id\":143459355679813632,\"lastUpdateTime\":1517403542000,\"monitorType\":\"task_ecommerce\",\"status\":2,\"taskAttributes\":{\"idCard\":\"1$==QAG4qCG6YqbZ5B7x4jZAAAYTdnqCA1U33ohkj8YTtIGEAAAAwA\",\"mobile\":\"1$h1F8RLAXKjQ/jJTw4YvIjQAAAAwA\",\"name\":\"1$uTiLQUhWyEEnvx/Qv3uilMAAAAwA\"},\"taskSteps\":[{\"stepCode\":\"create\",\"stepIndex\":1,\"stepName\":\"创建任务\"},{\"stepCode\":\"login\",\"stepIndex\":3,\"stepName\":\"登录\"},{\"stepCode\":\"crawl\",\"stepIndex\":4,\"stepName\":\"抓取\"},{\"stepCode\":\"process\",\"stepIndex\":5,\"stepName\":\"洗数\"}],\"uniqueId\":\"test\",\"webSite\":\"taobao.com\"}";
        map = JSON.parseObject(json);
        System.out.println(calculator.calculate(map, "(#taskSteps.?[#this[stepCode] == \"create\"]).size()>0?1:0"));
        System.out.println(calculator.calculate(map, "\"virtual_total_stat_appId\""));
        System.out.println(calculator.calculate(map, "#day(#createTime)"));
    }

}
