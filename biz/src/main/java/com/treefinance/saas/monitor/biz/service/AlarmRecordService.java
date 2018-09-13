package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmRecordStatus;
import com.treefinance.saas.monitor.dao.entity.AlarmRecord;
import com.treefinance.saas.monitor.dao.entity.AlarmRecordCriteria;
import com.treefinance.saas.monitor.dao.entity.AlarmWorkOrder;
import com.treefinance.saas.monitor.dao.entity.WorkOrderLog;

import java.util.Date;
import java.util.List;

/**
 * @author chengtong
 * @date 18/5/28 09:40
 */
public interface AlarmRecordService {


    /**
     * 插入
     *
     * @param alarmRecord 插入的数据
     */
    void insert(AlarmRecord alarmRecord);

    /**
     * 查询 分页
     *
     * @param criteria condition
     * @return 列表数据
     */
    List<AlarmRecord> queryPaginateByCondition(AlarmRecordCriteria criteria);

    /**
     *
     *
     * */
    List<AlarmRecord> queryAllUnprocessed(AlarmRecord alarmRecord);

    /**
     *
     *
     * */
    List<AlarmRecord> queryByCondition(AlarmRecordCriteria criteria);

    /**
     * 计数
     *
     * @param criteria condition
     * @return 数量
     */
    long countByExample(AlarmRecordCriteria criteria);

    /**
     * 获取没有处理的预警记录
     *
     * @param level   预警等级
     * @param summary 摘要信息
     * @param status  状态

     * @return 满足条件的最早的记录
     */
    AlarmRecord getFirstStatusRecord(EAlarmLevel level, String summary, EAlarmRecordStatus status);

    /**
     * 保存预警记录、工单、工单操作记录
     *
     * @param order    工单
     * @param orderLog 操作记录
     * @param record   记录
     */
    void saveAlarmRecords(AlarmWorkOrder order, AlarmRecord record, WorkOrderLog orderLog);

    /**
     * 获取特定的某一条记录
     *
     * @param id 主键
     * @return 数据记录
     */
    AlarmRecord getByPrimaryKey(Long id);


    /**
     * 保存预警记录、工单、工单操作记录
     *
     * @param order    工单
     * @param orderLog 操作记录
     * @param record   预警记录
     */
    void repairAlarmRecord(AlarmWorkOrder order, AlarmRecord record, WorkOrderLog orderLog);


    /**
     * 查询当天error等级的预警和记录
     *
     *
     * @param bizType
     * @param startTime
     * @param endTime*/
    List<AlarmRecord> queryTodayErrorList(String bizType, Date startTime, Date endTime,Integer offset,Integer pageSize);



    Integer countAlarmRecordInBizType(String bizType, Date startTime, Date endTime);
}
