package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.WorkOrderLog;
import com.treefinance.saas.monitor.dao.entity.WorkOrderLogCriteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface WorkOrderLogMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table work_order_log
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    long countByExample(WorkOrderLogCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table work_order_log
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int deleteByExample(WorkOrderLogCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table work_order_log
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table work_order_log
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int insert(WorkOrderLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table work_order_log
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int insertSelective(WorkOrderLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table work_order_log
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    List<WorkOrderLog> selectByExampleWithRowbounds(WorkOrderLogCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table work_order_log
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    List<WorkOrderLog> selectByExample(WorkOrderLogCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table work_order_log
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    List<WorkOrderLog> selectPaginationByExample(WorkOrderLogCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table work_order_log
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int insertOrUpdateBySelective(WorkOrderLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table work_order_log
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    void batchInsert(List<WorkOrderLog> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table work_order_log
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    void batchUpdateByPrimaryKey(List<WorkOrderLog> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table work_order_log
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<WorkOrderLog> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table work_order_log
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    WorkOrderLog selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table work_order_log
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int updateByExampleSelective(@Param("record") WorkOrderLog record, @Param("example") WorkOrderLogCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table work_order_log
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int updateByExample(@Param("record") WorkOrderLog record, @Param("example") WorkOrderLogCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table work_order_log
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int updateByPrimaryKeySelective(WorkOrderLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table work_order_log
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int updateByPrimaryKey(WorkOrderLog record);
}