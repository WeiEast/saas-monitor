package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.OperatorAllStatDayAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorAllStatDayAccessCriteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface OperatorAllStatDayAccessMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Tue Apr 17 17:21:18 CST 2018
     */
    long countByExample(OperatorAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Tue Apr 17 17:21:18 CST 2018
     */
    int deleteByExample(OperatorAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Tue Apr 17 17:21:18 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Tue Apr 17 17:21:18 CST 2018
     */
    int insert(OperatorAllStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Tue Apr 17 17:21:18 CST 2018
     */
    int insertSelective(OperatorAllStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Tue Apr 17 17:21:18 CST 2018
     */
    List<OperatorAllStatDayAccess> selectByExampleWithRowbounds(OperatorAllStatDayAccessCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Tue Apr 17 17:21:18 CST 2018
     */
    List<OperatorAllStatDayAccess> selectByExample(OperatorAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Tue Apr 17 17:21:18 CST 2018
     */
    List<OperatorAllStatDayAccess> selectPaginationByExample(OperatorAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Tue Apr 17 17:21:18 CST 2018
     */
    int insertOrUpdateBySelective(OperatorAllStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Tue Apr 17 17:21:18 CST 2018
     */
    void batchInsert(List<OperatorAllStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Tue Apr 17 17:21:18 CST 2018
     */
    void batchUpdateByPrimaryKey(List<OperatorAllStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Tue Apr 17 17:21:18 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<OperatorAllStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Tue Apr 17 17:21:18 CST 2018
     */
    OperatorAllStatDayAccess selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Tue Apr 17 17:21:18 CST 2018
     */
    int updateByExampleSelective(@Param("record") OperatorAllStatDayAccess record, @Param("example") OperatorAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Tue Apr 17 17:21:18 CST 2018
     */
    int updateByExample(@Param("record") OperatorAllStatDayAccess record, @Param("example") OperatorAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Tue Apr 17 17:21:18 CST 2018
     */
    int updateByPrimaryKeySelective(OperatorAllStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Tue Apr 17 17:21:18 CST 2018
     */
    int updateByPrimaryKey(OperatorAllStatDayAccess record);
}