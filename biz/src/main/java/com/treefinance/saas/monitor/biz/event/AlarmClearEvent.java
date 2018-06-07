package com.treefinance.saas.monitor.biz.event;

import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
import com.treefinance.saas.monitor.common.enumeration.EOrderStatus;
import com.treefinance.saas.monitor.dao.entity.AlarmRecord;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chengtong
 * @date 18/5/31 13:58
 */
@Setter
@Getter
public class AlarmClearEvent {

    private EAlarmType alarmType;

    private AlarmRecord alarmRecord;

    private String opDesc;

    private String processor;

    private String dutyMan;

    private EOrderStatus result;

}
