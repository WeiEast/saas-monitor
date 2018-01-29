package com.treefinance.saas.monitor.biz.autostat.basicdata.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.autostat.basicdata.filter.BasicDataFilter;
import com.treefinance.saas.monitor.biz.autostat.basicdata.filter.BasicDataFilterContext;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

/**
 * Created by yh-treefinance on 2018/1/23.
 */
public class BasicDataMessageListener implements MessageListenerConcurrently {

    private final static Logger logger = LoggerFactory.getLogger(BasicDataMessageListener.class);

    /**
     * 基础数据ID
     */
    private Long basicDataId;
    /**
     * 基础数据编码
     */
    private String basicDataCode;
    /**
     * 基础数据过滤器上下文
     */
    private BasicDataFilterContext basicDataFilterContext;

    public BasicDataMessageListener(Long basicDataId, String basicDataCode, BasicDataFilterContext basicDataFilterContext) {
        this.basicDataId = basicDataId;
        this.basicDataCode = basicDataCode;
        this.basicDataFilterContext = basicDataFilterContext;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        if (CollectionUtils.isEmpty(msgs)) {
            logger.info("收到消息无效==>{}", msgs);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        List<JSONObject> jsonObjects = Lists.newArrayList();
        for (MessageExt messageExt : msgs) {
            String json = new String(messageExt.getBody());
            jsonObjects.add(JSON.parseObject(json));
        }
        List<BasicDataFilter> filters = basicDataFilterContext.getBasicDataFilters(basicDataId);
        if (CollectionUtils.isNotEmpty(filters)) {
            for (BasicDataFilter filter : filters) {
                filter.doFilter(jsonObjects);
            }
        }
        return CONSUME_SUCCESS;
    }

    public Long getBasicDataId() {
        return basicDataId;
    }

    public String getBasicDataCode() {
        return basicDataCode;
    }
}
