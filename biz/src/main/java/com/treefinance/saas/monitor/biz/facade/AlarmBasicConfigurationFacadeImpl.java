package com.treefinance.saas.monitor.biz.facade;

import com.treefinance.saas.monitor.biz.service.AlarmService;
import com.treefinance.saas.monitor.biz.service.AsAlarmService;
import com.treefinance.saas.monitor.biz.service.AsAlarmTriggerRecordService;
import com.treefinance.saas.monitor.dao.entity.AsAlarm;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTrigger;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTriggerRecord;
import com.treefinance.saas.monitor.facade.domain.request.AlarmExcuteLogRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AlarmBasicConfigurationRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.AlarmExecuteLogRO;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmRO;
import com.treefinance.saas.monitor.facade.service.autoalarm.AlarmBasicConfigurationFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/7/19上午11:30
 */
@Service("alarmBasicConfigurationFacade")
public class AlarmBasicConfigurationFacadeImpl implements AlarmBasicConfigurationFacade {
    private static final Logger logger = LoggerFactory.getLogger(AlarmBasicConfigurationFacade.class);

    @Autowired
    private AsAlarmService AsalarmService;

    @Autowired
    private AsAlarmTriggerRecordService asAlarmTriggerRecordService;


    @Override
    public void add(){}

    @Override
    public void update(){}

    @Override
    public MonitorResult<AlarmExecuteLogRO> queryAlaramExecuteLogByAlarmId(AlarmExcuteLogRequest alarmExcuteLogRequest) {
        if(alarmExcuteLogRequest.getId()==null)
        {
            return new MonitorResult<>("执行日志查询id不能为空");
        }
        AsAlarm asAlarm = AsalarmService.getAsAlarmByPrimaryKey(alarmExcuteLogRequest.getId());
//        AsAlarmTrigger asAlarmTrigger =
        return  null;

    }

    @Override
    public List<AsAlarmRO> queryAlarmConfigurationList(AlarmBasicConfigurationRequest request) {
        return null;
    }
}
