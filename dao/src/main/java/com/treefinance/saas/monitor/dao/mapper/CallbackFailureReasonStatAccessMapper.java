package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.CallbackFailureReasonStatAccess;
import com.treefinance.saas.monitor.dao.entity.CallbackFailureReasonStatAccessCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface CallbackFailureReasonStatAccessMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    long countByExample(CallbackFailureReasonStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int deleteByExample(CallbackFailureReasonStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int insert(CallbackFailureReasonStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int insertSelective(CallbackFailureReasonStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    List<CallbackFailureReasonStatAccess> selectByExampleWithRowbounds(CallbackFailureReasonStatAccessCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    List<CallbackFailureReasonStatAccess> selectByExample(CallbackFailureReasonStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    List<CallbackFailureReasonStatAccess> selectPaginationByExample(CallbackFailureReasonStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int insertOrUpdateBySelective(CallbackFailureReasonStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    void batchInsert(List<CallbackFailureReasonStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    void batchUpdateByPrimaryKey(List<CallbackFailureReasonStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<CallbackFailureReasonStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    CallbackFailureReasonStatAccess selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int updateByExampleSelective(@Param("record") CallbackFailureReasonStatAccess record, @Param("example") CallbackFailureReasonStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int updateByExample(@Param("record") CallbackFailureReasonStatAccess record, @Param("example") CallbackFailureReasonStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int updateByPrimaryKeySelective(CallbackFailureReasonStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    int updateByPrimaryKey(CallbackFailureReasonStatAccess record);
}