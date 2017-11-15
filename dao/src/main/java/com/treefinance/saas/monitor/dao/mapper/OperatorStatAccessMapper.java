package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.OperatorStatAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorStatAccessCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface OperatorStatAccessMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_access
     *
     * @mbg.generated Wed Nov 15 16:58:46 CST 2017
     */
    long countByExample(OperatorStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_access
     *
     * @mbg.generated Wed Nov 15 16:58:46 CST 2017
     */
    int deleteByExample(OperatorStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_access
     *
     * @mbg.generated Wed Nov 15 16:58:46 CST 2017
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_access
     *
     * @mbg.generated Wed Nov 15 16:58:46 CST 2017
     */
    int insert(OperatorStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_access
     *
     * @mbg.generated Wed Nov 15 16:58:46 CST 2017
     */
    int insertSelective(OperatorStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_access
     *
     * @mbg.generated Wed Nov 15 16:58:46 CST 2017
     */
    List<OperatorStatAccess> selectByExampleWithRowbounds(OperatorStatAccessCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_access
     *
     * @mbg.generated Wed Nov 15 16:58:46 CST 2017
     */
    List<OperatorStatAccess> selectByExample(OperatorStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_access
     *
     * @mbg.generated Wed Nov 15 16:58:46 CST 2017
     */
    List<OperatorStatAccess> selectPaginationByExample(OperatorStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_access
     *
     * @mbg.generated Wed Nov 15 16:58:46 CST 2017
     */
    int insertOrUpdateBySelective(OperatorStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_access
     *
     * @mbg.generated Wed Nov 15 16:58:46 CST 2017
     */
    void batchInsert(List<OperatorStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_access
     *
     * @mbg.generated Wed Nov 15 16:58:46 CST 2017
     */
    void batchUpdateByPrimaryKey(List<OperatorStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_access
     *
     * @mbg.generated Wed Nov 15 16:58:46 CST 2017
     */
    void batchUpdateByPrimaryKeySelective(List<OperatorStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_access
     *
     * @mbg.generated Wed Nov 15 16:58:46 CST 2017
     */
    OperatorStatAccess selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_access
     *
     * @mbg.generated Wed Nov 15 16:58:46 CST 2017
     */
    int updateByExampleSelective(@Param("record") OperatorStatAccess record, @Param("example") OperatorStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_access
     *
     * @mbg.generated Wed Nov 15 16:58:46 CST 2017
     */
    int updateByExample(@Param("record") OperatorStatAccess record, @Param("example") OperatorStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_access
     *
     * @mbg.generated Wed Nov 15 16:58:46 CST 2017
     */
    int updateByPrimaryKeySelective(OperatorStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_access
     *
     * @mbg.generated Wed Nov 15 16:58:46 CST 2017
     */
    int updateByPrimaryKey(OperatorStatAccess record);
}