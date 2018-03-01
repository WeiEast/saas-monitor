package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatDayAccess;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatDayAccessCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface EcommerceAllStatDayAccessMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_day_access
     *
     * @mbg.generated Mon Jan 22 16:04:51 CST 2018
     */
    long countByExample(EcommerceAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_day_access
     *
     * @mbg.generated Mon Jan 22 16:04:51 CST 2018
     */
    int deleteByExample(EcommerceAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_day_access
     *
     * @mbg.generated Mon Jan 22 16:04:51 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_day_access
     *
     * @mbg.generated Mon Jan 22 16:04:51 CST 2018
     */
    int insert(EcommerceAllStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_day_access
     *
     * @mbg.generated Mon Jan 22 16:04:51 CST 2018
     */
    int insertSelective(EcommerceAllStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_day_access
     *
     * @mbg.generated Mon Jan 22 16:04:51 CST 2018
     */
    List<EcommerceAllStatDayAccess> selectByExampleWithRowbounds(EcommerceAllStatDayAccessCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_day_access
     *
     * @mbg.generated Mon Jan 22 16:04:51 CST 2018
     */
    List<EcommerceAllStatDayAccess> selectByExample(EcommerceAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_day_access
     *
     * @mbg.generated Mon Jan 22 16:04:51 CST 2018
     */
    List<EcommerceAllStatDayAccess> selectPaginationByExample(EcommerceAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_day_access
     *
     * @mbg.generated Mon Jan 22 16:04:51 CST 2018
     */
    int insertOrUpdateBySelective(EcommerceAllStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_day_access
     *
     * @mbg.generated Mon Jan 22 16:04:51 CST 2018
     */
    void batchInsert(List<EcommerceAllStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_day_access
     *
     * @mbg.generated Mon Jan 22 16:04:51 CST 2018
     */
    void batchUpdateByPrimaryKey(List<EcommerceAllStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_day_access
     *
     * @mbg.generated Mon Jan 22 16:04:51 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<EcommerceAllStatDayAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_day_access
     *
     * @mbg.generated Mon Jan 22 16:04:51 CST 2018
     */
    EcommerceAllStatDayAccess selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_day_access
     *
     * @mbg.generated Mon Jan 22 16:04:51 CST 2018
     */
    int updateByExampleSelective(@Param("record") EcommerceAllStatDayAccess record, @Param("example") EcommerceAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_day_access
     *
     * @mbg.generated Mon Jan 22 16:04:51 CST 2018
     */
    int updateByExample(@Param("record") EcommerceAllStatDayAccess record, @Param("example") EcommerceAllStatDayAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_day_access
     *
     * @mbg.generated Mon Jan 22 16:04:51 CST 2018
     */
    int updateByPrimaryKeySelective(EcommerceAllStatDayAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_day_access
     *
     * @mbg.generated Mon Jan 22 16:04:51 CST 2018
     */
    int updateByPrimaryKey(EcommerceAllStatDayAccess record);
}