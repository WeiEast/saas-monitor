package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.dao.entity.AsAlarmTriggerRecord;
import com.treefinance.saas.monitor.facade.domain.request.AlarmExcuteLogRequest;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/7/18下午8:28
 */
public interface AsAlarmTriggerRecordService {
    /**
     * 根据预警配置ID或触发时间段返回预警触发记录(分页)
     * @param alarmExcuteLogRequest
     * @return
     */
    List<AsAlarmTriggerRecord> queryAsAlarmTriggerRecordPagination(AlarmExcuteLogRequest alarmExcuteLogRequest);

    /**
     * 根据预警配置ID或触发时间段返回预警触发记录总数
     * @param alarmExcuteLogRequest
     * @return
     */
    long queryAsAlarmTriggerRecord(AlarmExcuteLogRequest alarmExcuteLogRequest);
}
