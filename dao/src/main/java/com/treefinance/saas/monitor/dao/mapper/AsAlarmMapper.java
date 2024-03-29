package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.AsAlarm;
import com.treefinance.saas.monitor.dao.entity.AsAlarmCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AsAlarmMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    long countByExample(AsAlarmCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    int deleteByExample(AsAlarmCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    int insert(AsAlarm record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    int insertSelective(AsAlarm record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    List<AsAlarm> selectByExampleWithRowbounds(AsAlarmCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    List<AsAlarm> selectByExample(AsAlarmCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    List<AsAlarm> selectPaginationByExample(AsAlarmCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    int insertOrUpdateBySelective(AsAlarm record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    void batchInsert(List<AsAlarm> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    void batchUpdateByPrimaryKey(List<AsAlarm> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<AsAlarm> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    AsAlarm selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    int updateByExampleSelective(@Param("record") AsAlarm record, @Param("example") AsAlarmCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    int updateByExample(@Param("record") AsAlarm record, @Param("example") AsAlarmCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    int updateByPrimaryKeySelective(AsAlarm record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    int updateByPrimaryKey(AsAlarm record);
}