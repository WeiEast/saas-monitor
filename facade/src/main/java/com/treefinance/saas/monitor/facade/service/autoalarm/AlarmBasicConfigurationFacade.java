package com.treefinance.saas.monitor.facade.service.autoalarm;

import com.treefinance.saas.monitor.facade.domain.request.AlarmExcuteLogRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AlarmBasicConfigurationDetailRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AlarmBasicConfigurationRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.AlarmExecuteLogRO;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmRO;

import java.util.List;

/**
 * 预警基本配置管理
 *
 * @author haojiahong
 * @date 2018/7/19
 */
public interface AlarmBasicConfigurationFacade {

    /**
     * 添加或修改预警配置
     */
    MonitorResult<Void> addOrUpdate(AlarmBasicConfigurationDetailRequest request);

    /**
     * 列表返回预警触发条件（分页）
     *
     * @param alarmExcuteLogRequest
     */
    MonitorResult<List<AlarmExecuteLogRO>> queryAlaramExecuteLogList(AlarmExcuteLogRequest alarmExcuteLogRequest);

    /**
     *预警配置管理分页列表
     * */
    MonitorResult<List<AsAlarmRO>> queryAlarmConfigurationList(AlarmBasicConfigurationRequest request);
}
