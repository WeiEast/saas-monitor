package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.OperatorStatDayAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorStatDayAccessCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface OperatorStatDayAccessMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    long countByExample(OperatorStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    int deleteByExample(OperatorStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    int insert(OperatorStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    int insertSelective(OperatorStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    List<OperatorStatDayAccess> selectByExampleWithRowbounds(OperatorStatDayAccessCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    List<OperatorStatDayAccess> selectByExample(OperatorStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    List<OperatorStatDayAccess> selectPaginationByExample(OperatorStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    int insertOrUpdateBySelective(OperatorStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    void batchInsert(List<OperatorStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    void batchUpdateByPrimaryKey(List<OperatorStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<OperatorStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    OperatorStatDayAccess selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    int updateByExampleSelective(@Param("record") OperatorStatDayAccess record, @Param("example") OperatorStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    int updateByExample(@Param("record") OperatorStatDayAccess record, @Param("example") OperatorStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    int updateByPrimaryKeySelective(OperatorStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    int updateByPrimaryKey(OperatorStatDayAccess record);
}