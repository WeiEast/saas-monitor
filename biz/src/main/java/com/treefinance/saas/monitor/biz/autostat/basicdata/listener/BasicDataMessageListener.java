package com.treefinance.saas.monitor.biz.autostat.basicdata.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.collect.Lists;
import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.monitor.biz.autostat.basicdata.filter.BasicDataFilter;
import com.treefinance.saas.monitor.biz.autostat.basicdata.filter.BasicDataFilterContext;
import com.treefinance.saas.monitor.context.SpringUtils;
import com.treefinance.saas.monitor.dao.entity.AsBasicDataHistory;
import com.treefinance.saas.monitor.dao.mapper.AsBasicDataHistoryMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

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

    private AsBasicDataHistoryMapper asBasicDataMapper;

    public BasicDataMessageListener(Long basicDataId, String basicDataCode, BasicDataFilterContext basicDataFilterContext, AsBasicDataHistoryMapper asBasicDataMapper) {
        this.basicDataId = basicDataId;
        this.basicDataCode = basicDataCode;
        this.basicDataFilterContext = basicDataFilterContext;
        this.asBasicDataMapper = asBasicDataMapper;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        List<AsBasicDataHistory> datalist = Lists.newArrayList();
        try {

            if (CollectionUtils.isEmpty(msgs)) {
                logger.info("收到消息无效==>{}", msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            for (MessageExt msg : msgs) {
                String dataJson = new String(msg.getBody());
                try {
                    JSON.parseObject(dataJson);
                } catch (Exception e) {
                    logger.error("msg body is not json: basicId={}, basicCode={}, msgId={}, msgBody={} ", basicDataId, basicDataCode, msg.getMsgId(), dataJson, e);
                    continue;
                }
                AsBasicDataHistory basicData = new AsBasicDataHistory();
                basicData.setBasicDataCode(basicDataCode);
                basicData.setBasicDataId(basicDataId);
                basicData.setDataId(msg.getMsgId());
                basicData.setDataTime(new Date());
                basicData.setDataJson(dataJson);
                basicData.setId(SpringUtils.getBean(UidService.class).getId());
                datalist.add(basicData);
            }
            List<BasicDataFilter> filters = basicDataFilterContext.getBasicDataFilters(basicDataId);
            logger.info("收到消息==>{},filters={}", JSON.toJSONString(datalist), filters.size());
            if (CollectionUtils.isNotEmpty(filters)) {
                for (BasicDataFilter filter : filters) {
                    filter.doFilter(datalist);
                }
            }
            return CONSUME_SUCCESS;
        } catch (Exception e) {
            logger.error("收到消息，处理异常", e);
            throw e;
        } finally {
            if (CollectionUtils.isNotEmpty(datalist)) {
                asBasicDataMapper.batchInsert(datalist);
            }
        }
    }

    public Long getBasicDataId() {
        return basicDataId;
    }

    public String getBasicDataCode() {
        return basicDataCode;
    }
}
