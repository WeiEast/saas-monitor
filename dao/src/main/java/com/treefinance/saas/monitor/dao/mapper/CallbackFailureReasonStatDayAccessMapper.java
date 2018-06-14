package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.CallbackFailureReasonStatDayAccess;
import com.treefinance.saas.monitor.dao.entity.CallbackFailureReasonStatDayAccessCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface CallbackFailureReasonStatDayAccessMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    long countByExample(CallbackFailureReasonStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int deleteByExample(CallbackFailureReasonStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int insert(CallbackFailureReasonStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int insertSelective(CallbackFailureReasonStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    List<CallbackFailureReasonStatDayAccess> selectByExampleWithRowbounds(CallbackFailureReasonStatDayAccessCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    List<CallbackFailureReasonStatDayAccess> selectByExample(CallbackFailureReasonStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    List<CallbackFailureReasonStatDayAccess> selectPaginationByExample(CallbackFailureReasonStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int insertOrUpdateBySelective(CallbackFailureReasonStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    void batchInsert(List<CallbackFailureReasonStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    void batchUpdateByPrimaryKey(List<CallbackFailureReasonStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<CallbackFailureReasonStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    CallbackFailureReasonStatDayAccess selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int updateByExampleSelective(@Param("record") CallbackFailureReasonStatDayAccess record, @Param("example") CallbackFailureReasonStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int updateByExample(@Param("record") CallbackFailureReasonStatDayAccess record, @Param("example") CallbackFailureReasonStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int updateByPrimaryKeySelective(CallbackFailureReasonStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int updateByPrimaryKey(CallbackFailureReasonStatDayAccess record);
}