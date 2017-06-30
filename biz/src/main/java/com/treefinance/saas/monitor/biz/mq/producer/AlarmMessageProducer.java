package com.treefinance.saas.monitor.biz.mq.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.datatrees.notify.async.body.NotifyEnum;
import com.datatrees.notify.async.body.mail.MailBody;
import com.datatrees.notify.async.body.mail.MailEnum;
import com.datatrees.notify.async.body.wechat.WeChatBody;
import com.datatrees.notify.async.body.wechat.WeChatEnum;
import com.datatrees.notify.async.body.wechat.message.TXTMessage;
import com.datatrees.notify.async.util.BeanUtil;
import com.google.common.base.Splitter;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.common.enumeration.EStatType;
import com.treefinance.saas.monitor.dao.entity.MerchantStatAccess;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

/**
 * 预警消息
 */
@Service
public class AlarmMessageProducer {
    private static final Logger logger = LoggerFactory.getLogger(AlarmMessageProducer.class);

    private DefaultMQProducer producer;
    @Autowired
    private DiamondConfig diamondConfig;

    private static final String AGENT_ID = "67";

    @PostConstruct
    public void init() throws MQClientException {
        producer = new DefaultMQProducer(diamondConfig.getMonitorAlarmGroupName());
        producer.setNamesrvAddr(diamondConfig.getNamesrvAddr());
        producer.setMaxMessageSize(1024 * 1024 * 2);
        producer.start();
    }

    @PreDestroy
    public void destroy() {
        producer.shutdown();
    }

    /**
     * 发送邮箱预警消息
     *
     * @param data
     */
    public void sendMail(List<MerchantStatAccess> data, EStatType type) {
        String mails = diamondConfig.getMonitorAlarmMails();
        if (StringUtils.isEmpty(mails)) {
            logger.info("No mail list are configured，do not alarm : message={} ", JSON.toJSONString(data));
            return;
        }
        logger.info("send alarm mail to {} ", mails);
        String topic = diamondConfig.getMonitorAlarmTopic();
        String tag = diamondConfig.getMonitorAlarmMailTag();
        String key = UUID.randomUUID().toString() + "_" + tag;
        List<String> tolist = Splitter.on(",").splitToList(mails);

        MailBody body = new MailBody();
        //设置邮件方式，具体看枚举值
        body.setMailEnum(MailEnum.SIMPLE_MAIL);
        //设置业务线，预警设置为alarm
        body.setBusiness("alarm");
        //设置发送给谁
        body.setToList(tolist);
        body.setSubject(generateTitle(type));
        body.setBody(generateBody(data, type));
        logger.info("send alarm mail message : message={}", JSON.toJSONString(body));
        sendMessage(topic, tag, key, BeanUtil.objectToByte(body));
    }

    private String generateTitle(EStatType type) {
        return "saas-[" + type.getName() + "]任务成功率预警";
    }

    /**
     * @return
     */
    private String generateBody(List<MerchantStatAccess> data, EStatType type) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("您好，").append(generateTitle(type)).append("，监控数据如下，请及时处理：").append("\n");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        data.forEach(access -> {
            buffer.append("商户：" + access.getAppId() + ", 数据时间: " + fmt.format(access.getDataTime())
                    + ", 任务总数：" + access.getTotalCount()
                    + ", 成功率/成功数:" + access.getSuccessRate() + "% / " + access.getSuccessCount()
                    + ", 失败率/失败数: " + access.getFailRate() + "% / " + access.getFailCount()
                    + "，取消数:" + access.getCancelCount()
            ).append("\n");
        });
        return buffer.toString();
    }

    /**
     * 发送微信
     *
     * @param data
     * @param type
     */
    public void sendWebChart(List<MerchantStatAccess> data, EStatType type) {
        String topic = diamondConfig.getMonitorAlarmTopic();
        String tag = diamondConfig.getMonitorAlarmWebchartTag();
        String key = UUID.randomUUID().toString() + "_" + tag;

        WeChatBody body = new WeChatBody();
        body.setAgentId(AGENT_ID);
        TXTMessage msg = new TXTMessage();
        msg.setMessage(generateBody(data, type));
        body.setMessage(msg);
        body.setWeChatEnum(WeChatEnum.DASHU_AN_APP_TXT);
        body.setNotifyEnum(NotifyEnum.WECHAT);
        logger.info("send alarm webchat message : message={}", JSON.toJSONString(body));
        sendMessage(topic, tag, key, BeanUtil.objectToByte(body));
    }

    /**
     * 发送消息
     *
     * @param topic
     * @param tag
     * @param key
     * @param body
     *
     *
     */
    private void sendMessage(String topic, String tag, String key, byte[] body) {
        try {
            SendResult sendResult = producer.send(new Message(topic, tag, key, body));
            if (logger.isInfoEnabled()) {
                logger.info("已发送消息[topic={},tag={},key={},body={},发送状态={}]", topic, tag, key, body, JSON.toJSONString(sendResult));
            }
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                logger.error(String.format("发送MQ消息[topic=%s,tag=%s,key=%s,body=%s]到消息中间件失败,发送状态为%s", topic, tag, key, body, sendResult.getSendStatus()));
            }
        } catch (InterruptedException | RemotingException | MQClientException | MQBrokerException e) {
            logger.error(String.format("发送MQ消息[topic=%s,tag=%s,key=%s,body=%s]到消息中间件失败", topic, tag, key, body), e);
        }
    }

}
