package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.OperatorAllStatDayAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorAllStatDayAccessCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface OperatorAllStatDayAccessMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Wed Nov 15 11:48:51 CST 2017
     */
    long countByExample(OperatorAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Wed Nov 15 11:48:51 CST 2017
     */
    int deleteByExample(OperatorAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Wed Nov 15 11:48:51 CST 2017
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Wed Nov 15 11:48:51 CST 2017
     */
    int insert(OperatorAllStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Wed Nov 15 11:48:51 CST 2017
     */
    int insertSelective(OperatorAllStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Wed Nov 15 11:48:51 CST 2017
     */
    List<OperatorAllStatDayAccess> selectByExampleWithRowbounds(OperatorAllStatDayAccessCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Wed Nov 15 11:48:51 CST 2017
     */
    List<OperatorAllStatDayAccess> selectByExample(OperatorAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Wed Nov 15 11:48:51 CST 2017
     */
    List<OperatorAllStatDayAccess> selectPaginationByExample(OperatorAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Wed Nov 15 11:48:51 CST 2017
     */
    int insertOrUpdateBySelective(OperatorAllStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Wed Nov 15 11:48:51 CST 2017
     */
    void batchInsert(List<OperatorAllStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Wed Nov 15 11:48:51 CST 2017
     */
    void batchUpdateByPrimaryKey(List<OperatorAllStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Wed Nov 15 11:48:51 CST 2017
     */
    void batchUpdateByPrimaryKeySelective(List<OperatorAllStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Wed Nov 15 11:48:51 CST 2017
     */
    OperatorAllStatDayAccess selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Wed Nov 15 11:48:51 CST 2017
     */
    int updateByExampleSelective(@Param("record") OperatorAllStatDayAccess record, @Param("example") OperatorAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Wed Nov 15 11:48:51 CST 2017
     */
    int updateByExample(@Param("record") OperatorAllStatDayAccess record, @Param("example") OperatorAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Wed Nov 15 11:48:51 CST 2017
     */
    int updateByPrimaryKeySelective(OperatorAllStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Wed Nov 15 11:48:51 CST 2017
     */
    int updateByPrimaryKey(OperatorAllStatDayAccess record);
}