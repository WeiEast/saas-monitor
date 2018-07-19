package com.treefinance.saas.monitor.facade.service.autoalarm;

import com.treefinance.saas.monitor.facade.domain.request.AlarmExcuteLogRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.AlarmExecuteLogRO;

/**
 * 预警基本配置管理
 *
 * @author haojiahong
 * @date 2018/7/19
 */
public interface AlarmBasicConfigurationFacade {

    /**
     * 添加预警配置
     */
    void add();

    /**
     * 修改预警配置
     */
    void update();

    /**
     * 列表返回预警触发条件（分页）
     * @param alarmExcuteLogRequest
     */
    MonitorResult<AlarmExecuteLogRO> queryAlaramExecuteLogByAlarmId(AlarmExcuteLogRequest alarmExcuteLogRequest);

}
