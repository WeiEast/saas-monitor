package com.treefinance.saas.monitor.biz.autostat.template.calc.spel;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.autostat.model.AsConstants;
import com.treefinance.saas.monitor.biz.autostat.template.calc.ExpressionCalculator;
import com.treefinance.saas.monitor.biz.autostat.utils.CronUtils;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
    public Object calculate(Long expressionId, String expression, Map<String, Object> dataMap) {
        // 初始化
        initContext(AsConstants.EXPRESSION, expression);
        initContext(AsConstants.EXPRESSION_ID, expressionId);
        initContext(AsConstants.REDIS, redisTemplate);
        StatTemplate statTemplate = (StatTemplate) context.get().get(AsConstants.STAT_TEMPLATE);

        // 数据准备
        StandardEvaluationContext context = new StandardEvaluationContext();
        dataMap.keySet().forEach(key -> context.setVariable(key, dataMap.get(key)));
        Object value = null;
        try {
            initContext(AsConstants.DATA, dataMap);

            context.registerFunction("count", this.getClass().getDeclaredMethod("count", Object.class));
            context.registerFunction("distinct", this.getClass().getDeclaredMethod("distinct", Object[].class));
            context.registerFunction("exists", this.getClass().getDeclaredMethod("exists", Object[].class));
            context.registerFunction("day", this.getClass().getDeclaredMethod("day", Long.class));
            context.registerFunction("contains", this.getClass().getDeclaredMethod("contains", String.class, Object.class));
            context.registerFunction("containsSet", this.getClass().getDeclaredMethod("containsSet", String.class, Object.class));
            context.registerFunction("divide", this.getClass().getDeclaredMethod("divide", String.class, String.class, Integer.class));
            context.registerFunction("subtract", this.getClass().getDeclaredMethod("subtract", String.class, String.class));

            SpelExpression spelExpression = (SpelExpression) parser.parseExpression(expression);
            spelExpression.setEvaluationContext(context);
            value = spelExpression.getValue();
        } catch (Exception e) {
            logger.error(" spel calculate error:  expressionId={}, expression={} result={},dataMap={}, statTemplate={}",
                    expressionId, expression, value, JSON.toJSONString(dataMap), JSON.toJSONString(statTemplate), e);
            throw new RuntimeException(e);
        } finally {
            destroyContext(AsConstants.EXPRESSION);
            destroyContext(AsConstants.EXPRESSION_ID);
            destroyContext(AsConstants.GROUP);
            destroyContext(AsConstants.DATA);
            if (logger.isDebugEnabled()) {
                logger.debug("spel calculate :  expressionId={}, expression={} result={}, dataMap={}, statTemplate={}", expressionId, expression, value, JSON.toJSONString(dataMap), statTemplate.getTemplateCode());
            }
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
     * @param objects
     * @return
     */
    public static int distinct(Object... objects) {
        if (objects == null || objects.length <= 0) {
            return 0;
        }
        StatTemplate statTemplate = (StatTemplate) context.get().get(AsConstants.STAT_TEMPLATE);
        Long expressionId = (Long) context.get().get(AsConstants.EXPRESSION_ID);
        long timeInterval;
        Object group;
        if (statTemplate.getEffectiveTime() > 0) {
            group = ((Map<String, Object>) context.get().get(AsConstants.DATA)).get(AsConstants.DISTINCT_USER_GROUP);
            timeInterval = statTemplate.getEffectiveTime() * 60 * 1000L;

        } else {
            group = ((Map<String, Object>) context.get().get(AsConstants.DATA)).get(AsConstants.GROUP);
            timeInterval = CronUtils.getTimeInterval(statTemplate.getStatCron());
        }
        String redisKey = Joiner.on(":").useForNull("null").join(group, "distinct", expressionId);
        StringRedisTemplate redisTemplate = (StringRedisTemplate) context.get().get(AsConstants.REDIS);
        String uniqueString = Joiner.on(":").useForNull("null").join(Arrays.asList(objects));
        if (redisTemplate.boundSetOps(redisKey).isMember(uniqueString)) {
            logger.info("distinct : result=0, expressionId={},redisKey={},value={}", expressionId, redisKey, uniqueString);
            return 0;
        }
        redisTemplate.boundSetOps(redisKey).add(uniqueString);
        redisTemplate.boundSetOps(redisKey).expire(2 * timeInterval, TimeUnit.MILLISECONDS);
        logger.info("distinct : result=1, expressionId={},redisKey={},value={}", expressionId, redisKey, uniqueString);
        return 1;
    }

    /**
     * 是否存在
     *
     * @param objects
     * @return
     */
    public static boolean exists(Object... objects) {
        if (objects == null || objects.length <= 0) {
            return false;
        }
        StatTemplate statTemplate = (StatTemplate) context.get().get(AsConstants.STAT_TEMPLATE);
        Long expressionId = (Long) context.get().get(AsConstants.EXPRESSION_ID);
        long timeInterval;
        Object group;
        if (statTemplate.getEffectiveTime() > 0) {
            group = ((Map<String, Object>) context.get().get(AsConstants.DATA)).get(AsConstants.DISTINCT_USER_GROUP);
            timeInterval = statTemplate.getEffectiveTime() * 60 * 1000L;

        } else {
            group = ((Map<String, Object>) context.get().get(AsConstants.DATA)).get(AsConstants.GROUP);
            timeInterval = CronUtils.getTimeInterval(statTemplate.getStatCron());
        }

        List<Object> keys = Lists.newArrayList(group, "exists", expressionId);
        String redisKey = Joiner.on(":").useForNull("null").join(keys);
        StringRedisTemplate redisTemplate = (StringRedisTemplate) context.get().get(AsConstants.REDIS);
        String uniqueString = Joiner.on(":").useForNull("null").join(Arrays.asList(objects));
        if (redisTemplate.boundSetOps(redisKey).isMember(uniqueString)) {
            logger.info("exists : result=true, expressionId={},redisKey={},value={}", expressionId, redisKey, uniqueString);
            return true;
        }

        redisTemplate.boundSetOps(redisKey).add(uniqueString);
        redisTemplate.boundSetOps(redisKey).expire(2 * timeInterval, TimeUnit.MILLISECONDS);
        logger.info("exists : result=false, expressionId={},redisKey={},value={}", expressionId, redisKey, uniqueString);
        return false;
    }


    /**
     * 包含
     *
     * @param key
     * @param value
     * @return
     */

    public static boolean contains(String key, Object value) {
        Long expressionId = (Long) context.get().get(AsConstants.EXPRESSION_ID);
        StatTemplate statTemplate = (StatTemplate) context.get().get(AsConstants.STAT_TEMPLATE);
        Object group;
        if (statTemplate.getEffectiveTime() > 0) {
            group = ((Map<String, Object>) context.get().get(AsConstants.DATA)).get(AsConstants.DISTINCT_USER_GROUP);

        } else {
            group = ((Map<String, Object>) context.get().get(AsConstants.DATA)).get(AsConstants.GROUP);
        }
        List<Object> keys = Lists.newArrayList(group, "contains", expressionId, key);
        String redisKey = Joiner.on(":").useForNull("null").join(keys);

        String _value = (value == null ? "null" : value.toString());

        StringRedisTemplate redisTemplate = (StringRedisTemplate) context.get().get(AsConstants.REDIS);
        if (Boolean.TRUE.equals(redisTemplate.boundSetOps(redisKey).isMember(_value))) {
            logger.info("contains : result=true, expressionId={},redisKey={}", expressionId, redisKey);
            return true;
        }
        logger.info("contains : result=false, expressionId={},redisKey={}", expressionId, redisKey);
        return false;
    }

    /**
     * 包含设定
     *
     * @param key
     * @param value
     * @return
     */

    public static boolean containsSet(String key, Object value) {
        StatTemplate statTemplate = (StatTemplate) context.get().get(AsConstants.STAT_TEMPLATE);
        Long expressionId = (Long) context.get().get(AsConstants.EXPRESSION_ID);
        long timeInterval;
        Object group;
        if (statTemplate.getEffectiveTime() > 0) {
            group = ((Map<String, Object>) context.get().get(AsConstants.DATA)).get(AsConstants.DISTINCT_USER_GROUP);
            timeInterval = statTemplate.getEffectiveTime() * 60 * 1000L;

        } else {
            group = ((Map<String, Object>) context.get().get(AsConstants.DATA)).get(AsConstants.GROUP);
            timeInterval = CronUtils.getTimeInterval(statTemplate.getStatCron());
        }
        List<Object> keys = Lists.newArrayList(group, "contains", expressionId, key);
        String redisKey = Joiner.on(":").useForNull("null").join(keys);

        String _value = (value == null ? "null" : value.toString());

        StringRedisTemplate redisTemplate = (StringRedisTemplate) context.get().get(AsConstants.REDIS);
        redisTemplate.boundSetOps(redisKey).add(_value);
        redisTemplate.boundSetOps(redisKey).expire(2 * timeInterval, TimeUnit.MILLISECONDS);
        logger.info("containsSet : result=true, expressionId={},redisKey={}", expressionId, redisKey);
        return true;
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

    /**
     * 除法
     *
     * @param numeratorStr
     * @param denominatorStr
     * @param scale          保留小数位数
     * @return
     */
    public static String divide(String numeratorStr, String denominatorStr, Integer scale) {
        Long expressionId = (Long) context.get().get(AsConstants.EXPRESSION_ID);
        BigDecimal result;
        if (StringUtils.isBlank(numeratorStr) || StringUtils.isBlank(denominatorStr) || Integer.valueOf(denominatorStr) == 0) {
            result = BigDecimal.valueOf(0, 2);
            logger.info("divide : result={}, expressionId={},numerator={},denominator={},scale={}",
                    result, expressionId, numeratorStr, denominatorStr, scale);
            return result.toString();
        }
        Integer numerator = Integer.valueOf(numeratorStr);
        Integer denominator = Integer.valueOf(denominatorStr);
        if (scale == null) {
            scale = 2;
        }
        result = BigDecimal.valueOf(numerator)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(denominator), scale, BigDecimal.ROUND_HALF_UP);
        logger.info("divide : result={}, expressionId={},numerator={},denominator={},scale={}",
                result, expressionId, numerator, denominator, scale);
        return result.toString();
    }


    public static String subtract(String aStr, String bStr) {
        Long expressionId = (Long) context.get().get(AsConstants.EXPRESSION_ID);
        if (StringUtils.isBlank(aStr) || StringUtils.isBlank(bStr)) {
            logger.info("subtract : result=0, expressionId={},a={},b={}",
                    expressionId, aStr, bStr);
            return 0 + "";
        }
        Integer a = Integer.valueOf(aStr);
        Integer b = Integer.valueOf(bStr);
        Integer result = a - b;
        if (result >= 0) {
            logger.info("subtract : result={}, expressionId={},a={},b={}",
                    result, expressionId, aStr, bStr);
            return result + "";
        }
        logger.info("subtract : result=0, expressionId={},a={},b={}",
                expressionId, aStr, bStr);
        return 0 + "";
    }

    public static void main(String[] args) throws Exception {
        System.out.println(SpelExpressionCalculator.class.getDeclaredMethod("exists", Object[].class));
        System.out.println(DateFormatUtils.format(1517403542000L, "yyyy-MM-dd HH:mm:ss"));
        String json = "{\"appId\":\"QATestabcdefghQA\",\"bizType\":3,\"completeTime\":1516959226000,\"monitorType\":\"task\",\"status\":1,\"stepCode\":\"\",\"taskId\":141595882901499904,\"uniqueId\":\"test\"}";
        Map<String, Object> map = JSON.parseObject(json);
        SpelExpressionCalculator calculator = new SpelExpressionCalculator();
//        System.out.println(calculator.calculate(1L, "#distinct(#uniqueId)", map));

        json = "{\"bizType\":3,\"webSite\":\"\",\"monitorType\":\"task_callback_failure_reason\",\"saasEnv\":\"1\",\"taskAttributes\":{\"groupName\":\"中国联通\",\"groupCode\":\"ZHONG_GUO_10010\"},\"dataTime\":1525251302000,\"accountNo\":\"\",\"appId\":\"QATestabcdefghQA\",\"failureReason\":\"1\",\"distinctUserGroup\":\"saas-monitor:stat:callback-failure-time-share:index-1:distinct:user:2018-05-02 16:30:00:appId-QATestabcdefghQA:groupCode-ZHONG_GUO_10010:groupName-中国联通:saasEnv-0:dataType-0:bizType-3\",\"taskId\":176375711672594432,\"uniqueId\":\"test333\",\"status\":1,\"group\":\"saas-monitor:stat:callback-failure-time-share:index-1:2018-05-02 16:55:00:appId-QATestabcdefghQA:groupCode-ZHONG_GUO_10010:groupName-中国联通:saasEnv-0:dataType-0:bizType-3\"}";
        map = JSON.parseObject(json);
        System.out.println(calculator.calculate(1L, "#failureReason==\"1\"?1:0 ", map));
//        System.out.println(calculator.calculate(1L, "(#taskSteps.?[#this[stepCode] == \"create\"]).size()>0?1:0", map));
//        System.out.println(calculator.calculate(1L, "\"virtual_total_stat_appId\"", map));
//        System.out.println(calculator.calculate(1L, "#day(#createTime)", map));
//        System.out.println(calculator.calculate(1L, "#distinct(#uniqueId)", map));

//        String expression = "#attributes[callbackMsg]!=null?#attributes[callbackMsg]:null";
//
//        String json = "{\n" +
//                "    \"accountNo\":\"aaa@qq.com\",\n" +
//                "    \"appId\":\"QATestabcdefghQA\",\n" +
//                "    \"attributes\":{\n" +
//                "        \"callbackHttpCode\":200,\n" +
//                "        \"callbackCode\":200,\n" +
//                "        \"callbackMsg\":\"回调成功\"\n" +
//                "    },\n" +
//                "    \"bizType\":3,\n" +
//                "    \"completeTime\":1520857590063,\n" +
//                "    \"monitorType\":\"task\",\n" +
//                "    \"status\":2,\n" +
//                "    \"stepCode\":\"\",\n" +
//                "    \"taskId\":141595882901499900,\n" +
//                "    \"uniqueId\":\"test\",\n" +
//                "    \"webSite\":\"qq.com\"\n" +
//                "}";
//
//        String json2 = "{\n" +
//                "    \"accountNo\":\"aaa@qq.com\",\n" +
//                "    \"appId\":\"QATestabcdefghQA\",\n" +
//                "    \"attributes\":{},\n" +
//                "    \"bizType\":3,\n" +
//                "    \"completeTime\":1520857590063,\n" +
//                "    \"monitorType\":\"task\",\n" +
//                "    \"status\":2,\n" +
//                "    \"stepCode\":\"\",\n" +
//                "    \"taskId\":141595882901499900,\n" +
//                "    \"uniqueId\":\"test\",\n" +
//                "    \"webSite\":\"qq.com\"\n" +
//                "}";
//        Map<String, Object> map = JSON.parseObject(json2);
//        StandardEvaluationContext context = new StandardEvaluationContext();
//        map.keySet().forEach(key -> context.setVariable(key, map.get(key)));
//
//        ExpressionParser parser = new SpelExpressionParser();
//        SpelExpression exp = (SpelExpression) parser.parseExpression(expression);
//        exp.setEvaluationContext(context);
//
//        Object message = exp.getValue();
//        System.out.println(message);
    }

}
