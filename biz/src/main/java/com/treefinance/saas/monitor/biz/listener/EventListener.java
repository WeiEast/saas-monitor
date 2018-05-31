package com.treefinance.saas.monitor.biz.listener;

import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.event.AlarmClearEvent;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.AlarmRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author chengtong
 * @date 18/5/31 14:22
 */
@Component
public class EventListener {

    @Autowired
    private AlarmMessageProducer alarmMessageProducer;

    @Autowired
    private DiamondConfig diamondConfig;

    @org.springframework.context.event.EventListener
    public void alarmClearFire(AlarmClearEvent event){
        String processor = event.getProcessor();
        String dutyMan = event.getDutyMan();
        String opDesc = event.getOpDesc();
        AlarmRecord alarmRecord = event.getAlarmRecord();
        EAlarmType alarmType = event.getAlarmType();

        StringBuilder body = new StringBuilder();
        body.append("【预警解除】").append("【" + "saas-").append(diamondConfig.getMonitorEnvironment()).append("】");
        body.append(alarmType.getDesc()).append("\n数据时间：").append(MonitorDateUtils.format(alarmRecord.getDataTime()))
                .append("\n预警等级：").append(alarmRecord.getLevel()).append("\n预警记录编号").append(alarmRecord.getId()).append("\n").append("值班人员：").append(dutyMan).append("\n处理人员：").append(processor).append
                ("\n操作信息：").append(opDesc);

        alarmMessageProducer.sendWechantAlarm(body.toString());

    }

}
