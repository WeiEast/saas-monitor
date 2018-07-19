package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.dao.entity.AsAlarm;
import com.treefinance.saas.monitor.dao.entity.AsAlarmCriteria;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/7/19上午10:56
 */
public interface AsAlarmService {

    AsAlarm getAsAlarmByPrimaryKey(long id);

    List<AsAlarm> selectPaginationByExample(AsAlarmCriteria criteria);
}
