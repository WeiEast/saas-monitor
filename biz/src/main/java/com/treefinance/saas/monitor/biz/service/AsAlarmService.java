package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.dao.entity.AsAlarm;
import com.treefinance.saas.monitor.dao.entity.AsAlarmCriteria;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AlarmBasicConfigurationDetailRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AlarmBasicConfigurationRequest;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmBasicConfigurationDetailRO;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/7/19上午10:56
 */
public interface AsAlarmService {

    AsAlarm getAsAlarmByPrimaryKey(long id);

    List<AsAlarm> selectPaginationByExample(AsAlarmCriteria criteria);

    List<AsAlarm> queryPagingList(AlarmBasicConfigurationRequest request);

    void addOrUpdate(AlarmBasicConfigurationDetailRequest request);

    AsAlarmBasicConfigurationDetailRO queryAsAlarmBasicConfigurationDetailById(Long id);

    long countByCondition(AlarmBasicConfigurationRequest request);

    void updateAlarmSwitch(Long alarmId);

    void deleteById(Long alarmId);


    void copyAlarm(Long id);

}
