package com.treefinance.saas.monitor.biz.listener;

import com.datatrees.notify.async.body.mail.MailEnum;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.event.AlarmClearEvent;
import com.treefinance.saas.monitor.biz.event.OrderDelegateEvent;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.biz.service.IvrNotifyService;
import com.treefinance.saas.monitor.biz.service.SmsNotifyService;
import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.AlarmRecord;
import com.treefinance.saas.monitor.dao.entity.AlarmWorkOrder;
import com.treefinance.saas.monitor.dao.entity.SaasWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author chengtong
 * @date 18/5/31 14:22
 */
@Component
public class EventListener {

    @Autowired
    private AlarmMessageProducer alarmMessageProducer;
    @Autowired
    private IvrNotifyService ivrNotifyService;
    @Autowired
    private SmsNotifyService smsNotifyService;

    @Autowired
    private DiamondConfig diamondConfig;

    @org.springframework.context.event.EventListener
    public void alarmClearFire(AlarmClearEvent event) {
        String processor = event.getProcessor();
        String dutyMan = event.getDutyMan();
        String opDesc = event.getOpDesc();
        AlarmRecord alarmRecord = event.getAlarmRecord();
        EAlarmType alarmType = event.getAlarmType();

        String body = "【预警解除】" + "【" + "saas-" + diamondConfig.getMonitorEnvironment() + "】" +
                alarmType.getDesc() + "\n数据时间：" + MonitorDateUtils.format(alarmRecord.getDataTime()) +
                "\n预警等级：" + alarmRecord.getLevel() + "\n预警记录编号" + alarmRecord.getId() + "\n" + "值班人员：" + dutyMan + "\n处理人员：" + processor +
                "\n操作信息：" + opDesc;

        alarmMessageProducer.sendWechantAlarm(body);

    }

    @org.springframework.context.event.EventListener
    public void orderDelegateFire(OrderDelegateEvent event) {
        SaasWorker processor = event.getProcessor();
        AlarmRecord alarmRecord = event.getAlarmRecord();
        AlarmWorkOrder order = event.getAlarmWorkOrder();

        StringBuilder body = new StringBuilder();

        body.append("【预警处理】").append("【" + "saas-").append(diamondConfig.getMonitorEnvironment()).append("】");
        body.append("\n").append(processor.getName()).append("小伙伴你好").append("id为").append(order.getId()).append
                ("的预警工单已由").append(order.getDutyName()).append("转交给你，请及时处理。").append("地址为：").append(diamondConfig
                .getConsoleAddress());

        smsNotifyService.send(body.toString(), Collections.singletonList(processor.getMobile()));
        alarmMessageProducer.sendMail("预警工单处理", body.toString(), MailEnum.SIMPLE_MAIL, processor.getEmail());
        ivrNotifyService.notifyIvrToDutyMan(body.toString(),processor.getMobile(),processor.getName());
    }

}
