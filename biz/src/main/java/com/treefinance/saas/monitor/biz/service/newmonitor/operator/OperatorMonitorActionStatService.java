package com.treefinance.saas.monitor.biz.service.newmonitor.operator;

import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.facade.model.enums.ETaskOperatorMonitorStatus;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/14.
 */
public interface OperatorMonitorActionStatService {

    /**
     * 按任务统计,更新特定运营商一定时间间隔的数据
     *
     * @param intervalTime
     * @param message
     * @param status
     */
    void updateIntervalDataByTask(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status);

    /**
     * 按任务统计,更新特定运营商一天的数据
     *
     * @param intervalTime
     * @param message
     * @param status
     */
    void updateDayDataByTask(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status);

    /**
     * 按任务统计,更新所有运营商一定时间间隔的数据
     *
     * @param intervalTime
     * @param message
     * @param status
     */
    void updateAllIntervalDataByTask(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status);

    /**
     * 按任务统计,更新所有运营商一天的数据
     *
     * @param intervalTime
     * @param message
     * @param status
     */
    void updateAllDayDataByTask(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status);


    /**
     * 按用户统计,更新特定运营商一定时间间隔的数据
     *
     * @param intervalTime
     * @param message
     * @param status
     */
    void updateIntervalDataByUser(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status);

    /**
     * 按用户统计,更新特定运营商一天的数据
     *
     * @param intervalTime
     * @param message
     * @param status
     */
    void updateDayDataByUser(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status);

    /**
     * 按用户统计,更新所有运营商一定时间间隔的数据
     *
     * @param intervalTime
     * @param message
     * @param status
     */
    void updateAllIntervalDataByUser(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status);

    /**
     * 按用户统计,更新所有运营商一天的数据
     *
     * @param intervalTime
     * @param message
     * @param status
     */
    void updateAllDayDataByUser(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status);

}
