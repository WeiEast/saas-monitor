package com.treefinance.saas.monitor.biz.autostat.basicdata.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.autostat.basicdata.filter.BasicDataFilterContext;
import com.treefinance.saas.monitor.biz.autostat.basicdata.listener.BasicDataMessageListener;
import com.treefinance.saas.monitor.biz.autostat.basicdata.service.BasicDataService;
import com.treefinance.saas.monitor.dao.entity.BasicData;
import com.treefinance.saas.monitor.dao.entity.BasicDataCriteria;
import com.treefinance.saas.monitor.dao.mapper.BasicDataMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by yh-treefinance on 2018/1/22.
 */
@Service
public class BasicDataServiceImpl implements BasicDataService {
    @Autowired
    private BasicDataMapper basicDataMapper;
    /**
     * 已经启动的监听器
     */
    private Map<String, DefaultMQPushConsumer> consumerContext = Maps.newConcurrentMap();

    @Autowired
    private BasicDataFilterContext basicDataFilterContext;


    @Override
    public List<BasicData> getAll() {
        return basicDataMapper.selectByExample(new BasicDataCriteria());
    }

    @Override
    public BasicData getByCode(String dataCode) {
        BasicDataCriteria criteria = new BasicDataCriteria();
        criteria.createCriteria().andDataCodeEqualTo(dataCode);
        List<BasicData> list = basicDataMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public BasicData getById(Long id) {
        return basicDataMapper.selectByPrimaryKey(id);
    }

    @Override
    public void startListener(String dataCode) {
        if (consumerContext.containsKey(dataCode)) {
            return;
        }
        BasicData basicData = getByCode(dataCode);
        Long basicDataId = basicData.getId();
        String basicDataCode = basicData.getDataCode();
        synchronized (this) {
            Map<String, Object> configMap = JSON.parseObject(basicData.getDataSourceConfigJson());
            String topic = (String) configMap.get("topic");
            String tag = (String) configMap.get("tag");
            String namesrvAddr = (String) configMap.get("namesrvAddr");
            String group = (String) configMap.get("group");

            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group);
            try {
                consumer.setNamesrvAddr(namesrvAddr);
                consumer.subscribe(topic, tag);
                consumer.setMessageModel(MessageModel.CLUSTERING);
                consumer.registerMessageListener(new BasicDataMessageListener(basicDataId, basicDataCode, basicDataFilterContext));
                consumer.start();
            } catch (MQClientException e) {
                throw new RuntimeException("start " + dataCode + " consumer error", e);
            }
            consumerContext.put(dataCode, consumer);
        }
    }
}
