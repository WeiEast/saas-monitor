package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.SaasStatDayAccess;
import com.treefinance.saas.monitor.dao.entity.SaasStatDayAccessCriteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface SaasStatDayAccessMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_day_access
     *
     * @mbg.generated Wed Apr 04 16:02:20 CST 2018
     */
    long countByExample(SaasStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_day_access
     *
     * @mbg.generated Wed Apr 04 16:02:20 CST 2018
     */
    int deleteByExample(SaasStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_day_access
     *
     * @mbg.generated Wed Apr 04 16:02:20 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_day_access
     *
     * @mbg.generated Wed Apr 04 16:02:20 CST 2018
     */
    int insert(SaasStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_day_access
     *
     * @mbg.generated Wed Apr 04 16:02:20 CST 2018
     */
    int insertSelective(SaasStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_day_access
     *
     * @mbg.generated Wed Apr 04 16:02:20 CST 2018
     */
    List<SaasStatDayAccess> selectByExampleWithRowbounds(SaasStatDayAccessCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_day_access
     *
     * @mbg.generated Wed Apr 04 16:02:20 CST 2018
     */
    List<SaasStatDayAccess> selectByExample(SaasStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_day_access
     *
     * @mbg.generated Wed Apr 04 16:02:20 CST 2018
     */
    List<SaasStatDayAccess> selectPaginationByExample(SaasStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_day_access
     *
     * @mbg.generated Wed Apr 04 16:02:20 CST 2018
     */
    int insertOrUpdateBySelective(SaasStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_day_access
     *
     * @mbg.generated Wed Apr 04 16:02:20 CST 2018
     */
    void batchInsert(List<SaasStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_day_access
     *
     * @mbg.generated Wed Apr 04 16:02:20 CST 2018
     */
    void batchUpdateByPrimaryKey(List<SaasStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_day_access
     *
     * @mbg.generated Wed Apr 04 16:02:20 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<SaasStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_day_access
     *
     * @mbg.generated Wed Apr 04 16:02:20 CST 2018
     */
    SaasStatDayAccess selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_day_access
     *
     * @mbg.generated Wed Apr 04 16:02:20 CST 2018
     */
    int updateByExampleSelective(@Param("record") SaasStatDayAccess record, @Param("example") SaasStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_day_access
     *
     * @mbg.generated Wed Apr 04 16:02:20 CST 2018
     */
    int updateByExample(@Param("record") SaasStatDayAccess record, @Param("example") SaasStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_day_access
     *
     * @mbg.generated Wed Apr 04 16:02:20 CST 2018
     */
    int updateByPrimaryKeySelective(SaasStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_day_access
     *
     * @mbg.generated Wed Apr 04 16:02:20 CST 2018
     */
    int updateByPrimaryKey(SaasStatDayAccess record);
}