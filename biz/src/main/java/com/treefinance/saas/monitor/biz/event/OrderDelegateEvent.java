package com.treefinance.saas.monitor.biz.event;

import com.treefinance.saas.monitor.dao.entity.AlarmRecord;
import com.treefinance.saas.monitor.dao.entity.AlarmWorkOrder;
import com.treefinance.saas.monitor.dao.entity.SaasWorker;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chengtong
 * @date 18/6/1 11:00
 */
@Setter
@Getter
public class OrderDelegateEvent {

    private SaasWorker processor;

    private AlarmRecord alarmRecord;

    private AlarmWorkOrder alarmWorkOrder;

    private String opName;
}
