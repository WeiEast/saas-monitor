package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.dao.entity.AsAlarmMsg;
import com.treefinance.saas.monitor.dao.entity.AsAlarmMsgCriteria;

import java.util.List;

public interface AsAlarmMsgService {

    List<AsAlarmMsg> selectByExample(AsAlarmMsgCriteria alarmMsgCriteria);

}
