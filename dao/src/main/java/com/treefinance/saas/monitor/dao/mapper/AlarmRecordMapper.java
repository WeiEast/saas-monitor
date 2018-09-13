package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.AlarmRecord;
import com.treefinance.saas.monitor.dao.entity.AlarmRecordCriteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.Date;
import java.util.List;

public interface AlarmRecordMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    long countByExample(AlarmRecordCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    int deleteByExample(AlarmRecordCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    int insert(AlarmRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    int insertSelective(AlarmRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    List<AlarmRecord> selectByExampleWithBLOBsWithRowbounds(AlarmRecordCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    List<AlarmRecord> selectByExampleWithBLOBs(AlarmRecordCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    List<AlarmRecord> selectByExampleWithRowbounds(AlarmRecordCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    List<AlarmRecord> selectByExample(AlarmRecordCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    List<AlarmRecord> selectPaginationByExample(AlarmRecordCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    int insertOrUpdateBySelective(AlarmRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    void batchInsert(List<AlarmRecord> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    void batchUpdateByPrimaryKey(List<AlarmRecord> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<AlarmRecord> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    AlarmRecord selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    int updateByExampleSelective(@Param("record") AlarmRecord record, @Param("example") AlarmRecordCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    int updateByExampleWithBLOBs(@Param("record") AlarmRecord record, @Param("example") AlarmRecordCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    int updateByExample(@Param("record") AlarmRecord record, @Param("example") AlarmRecordCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    int updateByPrimaryKeySelective(AlarmRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    int updateByPrimaryKeyWithBLOBs(AlarmRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table alarm_record
     *
     * @mbg.generated Tue Sep 04 15:37:29 CST 2018
     */
    int updateByPrimaryKey(AlarmRecord record);


    List<AlarmRecord> queryAlarmRecordInBizType(@Param("bizType")String bizType, @Param("startTime")Date startTime,
                                                @Param("endTime")Date endTime,@Param("offset")int offset,@Param
                                                        ("pageSize")int pageSize);

    Integer countInBizType(@Param("bizType")String bizType, @Param("startTime")Date startTime,
                           @Param("endTime")Date endTime);


}