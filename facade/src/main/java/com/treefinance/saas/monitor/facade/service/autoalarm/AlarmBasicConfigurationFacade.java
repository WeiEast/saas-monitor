package com.treefinance.saas.monitor.facade.service.autoalarm;

import com.treefinance.saas.monitor.facade.domain.request.AlarmExcuteLogRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AlarmBasicConfigurationDetailRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AlarmBasicConfigurationRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AlarmBasicConfigurationTestRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.AlarmExecuteLogRO;
import com.treefinance.saas.monitor.facade.domain.ro.SaasWorkerRO;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmBasicConfigurationDetailRO;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmRO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 预警基本配置管理
 *
 * @author haojiahong
 * @date 2018/7/19
 */
public interface AlarmBasicConfigurationFacade {

    /**
     * 添加或修改预警配置
     *
     * @param request
     * @return
     */
    MonitorResult<Void> addOrUpdate(AlarmBasicConfigurationDetailRequest request);


    /**
     * 根据预警配置id查询预警配置详细信息
     *
     * @param id 预警配置表主键id
     * @return
     */
    MonitorResult<AsAlarmBasicConfigurationDetailRO> queryAlarmConfigurationDetailById(Long id);

    /**
     * 列表返回预警触发条件（分页）
     *
     * @param alarmExcuteLogRequest
     */
    MonitorResult<List<AlarmExecuteLogRO>> queryAlaramExecuteLogList(AlarmExcuteLogRequest alarmExcuteLogRequest);

    /**
     * 预警配置管理分页列表
     */
    MonitorResult<List<AsAlarmRO>> queryAlarmConfigurationList(AlarmBasicConfigurationRequest request);

    /**
     * 根据cron表达式得到下次预警时间与预警间隔时间
     *
     * @param cronExpression
     * @return
     */
    MonitorResult<Map<String, String>> getCronComputeValue(String cronExpression);

    /**
     * 测试预警配置
     *
     * @return
     */
    MonitorResult<Object> testAlarmConfiguration(AlarmBasicConfigurationTestRequest request);

    /**
     * 根据日期返回对应的值班人员
     *
     * @param date
     * @return
     */
    MonitorResult<List<SaasWorkerRO>> queryWorkerNameByDate(Date date);

    /**
     * 打开或者关闭预警开关
     * @param alarmId
     * @return
     */
    MonitorResult<Object> updateAlarmSwitch(Long alarmId);


    /**
     * 删除预警配置
     * @param alarmId 编号
     *
     * */
    MonitorResult<Boolean> deleteById(Long alarmId);

    /**
     * 复制预警配置
     * @param alarmId alarmId
     * @return Boolean
     */
    MonitorResult<Boolean> duplicateConfig(Long alarmId);

}
