package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.AsAlarmTriggerRecord;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTriggerRecordCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AsAlarmTriggerRecordMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger_record
     *
     * @mbg.generated Mon Jul 23 20:30:06 CST 2018
     */
    long countByExample(AsAlarmTriggerRecordCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger_record
     *
     * @mbg.generated Mon Jul 23 20:30:06 CST 2018
     */
    int deleteByExample(AsAlarmTriggerRecordCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger_record
     *
     * @mbg.generated Mon Jul 23 20:30:06 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger_record
     *
     * @mbg.generated Mon Jul 23 20:30:06 CST 2018
     */
    int insert(AsAlarmTriggerRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger_record
     *
     * @mbg.generated Mon Jul 23 20:30:06 CST 2018
     */
    int insertSelective(AsAlarmTriggerRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger_record
     *
     * @mbg.generated Mon Jul 23 20:30:06 CST 2018
     */
    List<AsAlarmTriggerRecord> selectByExampleWithRowbounds(AsAlarmTriggerRecordCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger_record
     *
     * @mbg.generated Mon Jul 23 20:30:06 CST 2018
     */
    List<AsAlarmTriggerRecord> selectByExample(AsAlarmTriggerRecordCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger_record
     *
     * @mbg.generated Mon Jul 23 20:30:06 CST 2018
     */
    List<AsAlarmTriggerRecord> selectPaginationByExample(AsAlarmTriggerRecordCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger_record
     *
     * @mbg.generated Mon Jul 23 20:30:06 CST 2018
     */
    int insertOrUpdateBySelective(AsAlarmTriggerRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger_record
     *
     * @mbg.generated Mon Jul 23 20:30:06 CST 2018
     */
    void batchInsert(List<AsAlarmTriggerRecord> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger_record
     *
     * @mbg.generated Mon Jul 23 20:30:06 CST 2018
     */
    void batchUpdateByPrimaryKey(List<AsAlarmTriggerRecord> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger_record
     *
     * @mbg.generated Mon Jul 23 20:30:06 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<AsAlarmTriggerRecord> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger_record
     *
     * @mbg.generated Mon Jul 23 20:30:06 CST 2018
     */
    AsAlarmTriggerRecord selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger_record
     *
     * @mbg.generated Mon Jul 23 20:30:06 CST 2018
     */
    int updateByExampleSelective(@Param("record") AsAlarmTriggerRecord record, @Param("example") AsAlarmTriggerRecordCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger_record
     *
     * @mbg.generated Mon Jul 23 20:30:06 CST 2018
     */
    int updateByExample(@Param("record") AsAlarmTriggerRecord record, @Param("example") AsAlarmTriggerRecordCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger_record
     *
     * @mbg.generated Mon Jul 23 20:30:06 CST 2018
     */
    int updateByPrimaryKeySelective(AsAlarmTriggerRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger_record
     *
     * @mbg.generated Mon Jul 23 20:30:06 CST 2018
     */
    int updateByPrimaryKey(AsAlarmTriggerRecord record);
}