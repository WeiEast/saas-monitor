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
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.common.enumeration.EStatType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.MerchantStatAccess;
import com.treefinance.saas.monitor.dao.entity.SaasStatAccess;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
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


    public void sendMailAlarm(String title, String body) {
        try {
            String mails = diamondConfig.getMonitorAlarmMails();
            if (StringUtils.isEmpty(mails)) {
                logger.info("No mail list are configured，do not alarm : title={},body={} ", JSON.toJSONString(title), JSON.toJSONString(body));
                return;
            }
            logger.info("send alarm mail to {} ", mails);
            String topic = diamondConfig.getMonitorAlarmTopic();
            String tag = diamondConfig.getMonitorAlarmMailTag();
            String key = UUID.randomUUID().toString() + "_" + tag;
            List<String> tolist = Splitter.on(",").splitToList(mails);

            MailBody mailBody = new MailBody();
            //设置邮件方式，具体看枚举值
            mailBody.setMailEnum(MailEnum.SIMPLE_MAIL);
            //设置业务线，预警设置为alarm
            mailBody.setBusiness("alarm");
            //设置发送给谁
            mailBody.setToList(tolist);
            mailBody.setSubject(title);
            mailBody.setBody(body);
            logger.info("send alarm mail message : message={}", JSON.toJSONString(mailBody));
            sendMessage(topic, tag, key, BeanUtil.objectToByte(mailBody));
        } catch (Exception e) {
            logger.error("sendMail failed :", e);
        }
    }

    public void sendWechantAlarm(String bodyData) {
        try {
            String topic = diamondConfig.getMonitorAlarmTopic();
            String tag = diamondConfig.getMonitorAlarmWebchartTag();
            String key = UUID.randomUUID().toString() + "_" + tag;

            WeChatBody body = new WeChatBody();
            body.setAgentId(AGENT_ID);
            TXTMessage msg = new TXTMessage();
            msg.setMessage(bodyData);
            body.setMessage(msg);
            body.setWeChatEnum(WeChatEnum.DASHU_AN_APP_TXT);
            body.setNotifyEnum(NotifyEnum.WECHAT);
            logger.info("TaskMonitorAlarm:send alarm webchat message : message={}", JSON.toJSONString(body));
            sendMessage(topic, tag, key, BeanUtil.objectToByte(body));
        } catch (Exception e) {
            logger.error("sendWechat failed :", e);
        }
    }

    /**
     * 发送邮箱预警消息
     *
     * @param data
     */
    public void sendMail(List<MerchantStatAccess> data, EStatType type) {
        try {
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
        } catch (Exception e) {
            logger.error("sendMail failed : type=" + type + ", data=" + JSON.toJSONString(data), e);
        }
    }

    private String generateTitle(EStatType type) {
        return "saas-" + diamondConfig.getMonitorEnvironment() + "[" + type.getName() + "]任务成功率预警";
    }

    /**
     * @return
     */
    private String generateBody(List<MerchantStatAccess> data, EStatType type) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("您好，").append(generateTitle(type)).append("，监控数据如下，请及时处理：").append("\n");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<String> appIdList = Lists.newArrayList();
        List<String> dataTimeList = Lists.newArrayList();
        List<Integer> totalCountList = Lists.newArrayList();
        List<BigDecimal> successRateList = Lists.newArrayList();
        List<Integer> successCountList = Lists.newArrayList();
        List<BigDecimal> failRateList = Lists.newArrayList();
        List<Integer> failCountList = Lists.newArrayList();
        List<Integer> cancelCountList = Lists.newArrayList();
        data.forEach(access -> {
            String appId = access.getAppId();
            if (!appIdList.contains(appId)) {
                appIdList.add(appId);
            }
            dataTimeList.add(fmt.format(access.getDataTime()));
            totalCountList.add(access.getTotalCount());
            successRateList.add(access.getSuccessRate());
            successCountList.add(access.getSuccessCount());
            failRateList.add(access.getFailRate());
            failCountList.add(access.getFailCount());
            cancelCountList.add(access.getCancelCount());
        });


        buffer.append(" 商户: " + Joiner.on(" | ").useForNull(" ").join(appIdList) + " \n");
        buffer.append(" 数据时间: " + Joiner.on(" | ").useForNull(" ").join(dataTimeList) + " \n");
        buffer.append(" 任务总数: " + Joiner.on(" | ").useForNull(" ").join(totalCountList) + " \n");
        buffer.append(" 成功率(%): " + Joiner.on(" | ").useForNull(" ").join(successRateList) + " \n");
        buffer.append(" 成功数: " + Joiner.on(" | ").useForNull(" ").join(successCountList) + " \n");
        buffer.append(" 失败率(%): " + Joiner.on(" | ").useForNull(" ").join(failRateList) + " \n");
        buffer.append(" 失败数: " + Joiner.on(" | ").useForNull(" ").join(failCountList) + " \n");
        buffer.append(" 取消数: " + Joiner.on(" | ").useForNull(" ").join(cancelCountList) + " \n");
        return buffer.toString();
    }

    /**
     * 发送微信
     *
     * @param data
     * @param type
     */
    public void sendWebChart(List<MerchantStatAccess> data, EStatType type) {
        try {
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
        } catch (Exception e) {
            logger.error("sendWebChart failed : type=" + type + ", data=" + JSON.toJSONString(data), e);
        }
    }

    /**
     * 发送消息
     *
     * @param topic
     * @param tag
     * @param key
     * @param body
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

    public void sendMail4All(List<SaasStatAccess> data, EStatType type) {
        if (diamondConfig.getMonitorAlarmMailSwitch().equals("off")) {
            logger.info("TaskMonitorAlarm:monitor alarm mail switch is off");
            return;
        }
        try {
            String mails = diamondConfig.getMonitorAlarmMails();
            if (StringUtils.isEmpty(mails)) {
                logger.info("TaskMonitorAlarm:No mail list are configured，do not alarm : message={} ", JSON.toJSONString(data));
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
            body.setBody(generateAllBody(data, type));
            logger.info("TaskMonitorAlarm:send alarm mail message : message={}", JSON.toJSONString(body));
            sendMessage(topic, tag, key, BeanUtil.objectToByte(body));
        } catch (Exception e) {
            logger.error("TaskMonitorAlarm:sendMail failed : type={}, data={}", type.getName(), JSON.toJSONString(data), e);
        }
    }

    public void sendWebChart4All(List<SaasStatAccess> data, EStatType type) {
        if (diamondConfig.getMonitorAlarmWechatSwitch().equals("off")) {
            logger.info("TaskMonitorAlarm:monitor alarm wechat switch is off!");
            return;
        }
        try {
            String topic = diamondConfig.getMonitorAlarmTopic();
            String tag = diamondConfig.getMonitorAlarmWebchartTag();
            String key = UUID.randomUUID().toString() + "_" + tag;

            WeChatBody body = new WeChatBody();
            body.setAgentId(AGENT_ID);
            TXTMessage msg = new TXTMessage();
            msg.setMessage(generateAllBody(data, type));
            body.setMessage(msg);
            body.setWeChatEnum(WeChatEnum.DASHU_AN_APP_TXT);
            body.setNotifyEnum(NotifyEnum.WECHAT);
            logger.info("TaskMonitorAlarm:send alarm webchat message : message={}", JSON.toJSONString(body));
            sendMessage(topic, tag, key, BeanUtil.objectToByte(body));
        } catch (Exception e) {
            logger.error("TaskMonitorAlarm:sendWechat failed :type={},data={}", type.getName(), JSON.toJSONString(data));
        }
    }


    /**
     * 运营商监控预警(邮件)
     *
     * @param title
     * @param dataBody
     */
    public void sendMail4OperatorMonitor(String title, String dataBody, Date jobTime) {
        try {
            String mails = diamondConfig.getMonitorAlarmMails();
            if (StringUtils.isEmpty(mails)) {
                logger.info("运营商监控,预警定时任务执行jobTime={},未配置邮件接收人,邮件发送失败,title={},dataBody={}",
                        MonitorDateUtils.format(jobTime), title, dataBody);
                return;
            }
            logger.info("运营商监控,预警定时任务执行jobTime={},发送邮件到mails={}",
                    MonitorDateUtils.format(jobTime), mails);
            String topic = diamondConfig.getMonitorAlarmTopic();
            String tag = diamondConfig.getMonitorAlarmMailTag();
            String key = UUID.randomUUID().toString() + "_" + tag;
            List<String> tolist = Splitter.on(",").splitToList(mails);

            MailBody body = new MailBody();
            //设置邮件方式，具体看枚举值
            body.setMailEnum(MailEnum.HTML_MAIL);
            //设置业务线，预警设置为alarm
            body.setBusiness("alarm");
            //设置发送给谁
            body.setToList(tolist);
            body.setSubject(title);
            body.setBody(dataBody);
            logger.info("运营商监控,预警定时任务执行jobTime={},发送邮件message={}", MonitorDateUtils.format(jobTime), JSON.toJSONString(body));
            sendMessage(topic, tag, key, BeanUtil.objectToByte(body));
        } catch (Exception e) {
            logger.error("运营商监控,预警定时任务执行jobTime={},发送邮件异常", MonitorDateUtils.format(jobTime), e);
        }
    }


    /**
     * 运营商监控预警(微信)
     *
     * @param dataBody
     * @param jobTime
     */
    public void sendWebChart4OperatorMonitor(String dataBody, Date jobTime) {
        try {
            String topic = diamondConfig.getMonitorAlarmTopic();
            String tag = diamondConfig.getMonitorAlarmWebchartTag();
            String key = UUID.randomUUID().toString() + "_" + tag;

            WeChatBody body = new WeChatBody();
            body.setAgentId(AGENT_ID);
            TXTMessage msg = new TXTMessage();
            msg.setMessage(dataBody);
            body.setMessage(msg);
            body.setWeChatEnum(WeChatEnum.DASHU_AN_APP_TXT);
            body.setNotifyEnum(NotifyEnum.WECHAT);
            logger.info("运营商监控,预警定时任务执行jobTime={},发送微信message={}", MonitorDateUtils.format(jobTime), JSON.toJSONString(body));
            sendMessage(topic, tag, key, BeanUtil.objectToByte(body));
        } catch (Exception e) {
            logger.error("运营商监控,预警定时任务执行jobTime={},发送微信异常", MonitorDateUtils.format(jobTime), e);
        }

    }


    private String generateAllBody(List<SaasStatAccess> data, EStatType type) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("您好，").append(generateTitle(type)).append("，监控数据如下，请及时处理：").append("\n");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<String> dataTimeList = Lists.newArrayList();
        List<Integer> totalCountList = Lists.newArrayList();
        List<BigDecimal> successRateList = Lists.newArrayList();
        List<Integer> successCountList = Lists.newArrayList();
        List<BigDecimal> failRateList = Lists.newArrayList();
        List<Integer> failCountList = Lists.newArrayList();
        List<Integer> cancelCountList = Lists.newArrayList();
        data.forEach(access -> {
            dataTimeList.add(fmt.format(access.getDataTime()));
            totalCountList.add(access.getTotalCount());
            successRateList.add(calcRate(access.getSuccessCount(), access.getTotalCount()));
            successCountList.add(access.getSuccessCount());
            failRateList.add(calcRate(access.getFailCount(), access.getTotalCount()));
            failCountList.add(access.getFailCount());
            cancelCountList.add(access.getCancelCount());
        });

        buffer.append(" 数据时间: " + Joiner.on(" | ").useForNull(" ").join(dataTimeList) + " \n");
        buffer.append(" 任务总数: " + Joiner.on(" | ").useForNull(" ").join(totalCountList) + " \n");
        buffer.append(" 转化率(%): " + Joiner.on(" | ").useForNull(" ").join(successRateList) + " \n");
        buffer.append(" 成功数: " + Joiner.on(" | ").useForNull(" ").join(successCountList) + " \n");
        buffer.append(" 失败率(%): " + Joiner.on(" | ").useForNull(" ").join(failRateList) + " \n");
        buffer.append(" 失败数: " + Joiner.on(" | ").useForNull(" ").join(failCountList) + " \n");
        buffer.append(" 取消数: " + Joiner.on(" | ").useForNull(" ").join(cancelCountList) + " \n");
        return buffer.toString();
    }


    /**
     * 任务监控预警(邮件)
     *
     * @param title
     * @param dataBody
     */
    public void sendMail4TaskExistMonitor(String title, String dataBody) {
        try {
            String mails = diamondConfig.getMonitorAlarmMails();
            if (StringUtils.isEmpty(mails)) {
                logger.info("任务监控,未配置邮件接收人,邮件发送失败,title={},dataBody={}", title, dataBody);
                return;
            }
            logger.info("任务监控,预警定时任务执行,发送邮件到mails={}", mails);
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
            body.setSubject(title);
            body.setBody(dataBody);
            logger.info("任务监控,预警定时任务执行,发送邮件message={}", JSON.toJSONString(body));
            sendMessage(topic, tag, key, BeanUtil.objectToByte(body));
        } catch (Exception e) {
            logger.error("任务监控,预警定时任务执行,,发送邮件异常", e);
        }
    }

    /**
     * 任务监控预警(微信)
     *
     * @param dataBody
     */
    public void sendWebChart4TaskExistMonitor(String dataBody) {
        try {
            String topic = diamondConfig.getMonitorAlarmTopic();
            String tag = diamondConfig.getMonitorAlarmWebchartTag();
            String key = UUID.randomUUID().toString() + "_" + tag;

            WeChatBody body = new WeChatBody();
            body.setAgentId(AGENT_ID);
            TXTMessage msg = new TXTMessage();
            msg.setMessage(dataBody);
            body.setMessage(msg);
            body.setWeChatEnum(WeChatEnum.DASHU_AN_APP_TXT);
            body.setNotifyEnum(NotifyEnum.WECHAT);
            logger.info("任务监控,预警定时任务执行,发送微信message={}", JSON.toJSONString(body));
            sendMessage(topic, tag, key, BeanUtil.objectToByte(body));
        } catch (Exception e) {
            logger.error("任务监控,预警定时任务执行,发送微信异常", e);
        }

    }


    /**
     * 计算比率
     *
     * @param totalCount 总数
     * @param rateCount  比率数
     * @return
     */
    private BigDecimal calcRate(Integer rateCount, Integer totalCount) {
        if (totalCount == null || rateCount == null) {
            return null;
        }
        if (totalCount == 0) {
            return null;
        }
        BigDecimal rate = BigDecimal.valueOf(rateCount, 2)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalCount, 2), 2);
        return rate;
    }

}
