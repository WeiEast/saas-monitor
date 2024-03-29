package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatAccess;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatAccessCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface EcommerceAllStatAccessMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_access
     *
     * @mbg.generated Fri Jul 06 14:02:35 CST 2018
     */
    long countByExample(EcommerceAllStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_access
     *
     * @mbg.generated Fri Jul 06 14:02:35 CST 2018
     */
    int deleteByExample(EcommerceAllStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_access
     *
     * @mbg.generated Fri Jul 06 14:02:35 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_access
     *
     * @mbg.generated Fri Jul 06 14:02:35 CST 2018
     */
    int insert(EcommerceAllStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_access
     *
     * @mbg.generated Fri Jul 06 14:02:35 CST 2018
     */
    int insertSelective(EcommerceAllStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_access
     *
     * @mbg.generated Fri Jul 06 14:02:35 CST 2018
     */
    List<EcommerceAllStatAccess> selectByExampleWithRowbounds(EcommerceAllStatAccessCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_access
     *
     * @mbg.generated Fri Jul 06 14:02:35 CST 2018
     */
    List<EcommerceAllStatAccess> selectByExample(EcommerceAllStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_access
     *
     * @mbg.generated Fri Jul 06 14:02:35 CST 2018
     */
    List<EcommerceAllStatAccess> selectPaginationByExample(EcommerceAllStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_access
     *
     * @mbg.generated Fri Jul 06 14:02:35 CST 2018
     */
    int insertOrUpdateBySelective(EcommerceAllStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_access
     *
     * @mbg.generated Fri Jul 06 14:02:35 CST 2018
     */
    void batchInsert(List<EcommerceAllStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_access
     *
     * @mbg.generated Fri Jul 06 14:02:35 CST 2018
     */
    void batchUpdateByPrimaryKey(List<EcommerceAllStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_access
     *
     * @mbg.generated Fri Jul 06 14:02:35 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<EcommerceAllStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_access
     *
     * @mbg.generated Fri Jul 06 14:02:35 CST 2018
     */
    EcommerceAllStatAccess selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_access
     *
     * @mbg.generated Fri Jul 06 14:02:35 CST 2018
     */
    int updateByExampleSelective(@Param("record") EcommerceAllStatAccess record, @Param("example") EcommerceAllStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_access
     *
     * @mbg.generated Fri Jul 06 14:02:35 CST 2018
     */
    int updateByExample(@Param("record") EcommerceAllStatAccess record, @Param("example") EcommerceAllStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_access
     *
     * @mbg.generated Fri Jul 06 14:02:35 CST 2018
     */
    int updateByPrimaryKeySelective(EcommerceAllStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ecommerce_all_stat_access
     *
     * @mbg.generated Fri Jul 06 14:02:35 CST 2018
     */
    int updateByPrimaryKey(EcommerceAllStatAccess record);
}