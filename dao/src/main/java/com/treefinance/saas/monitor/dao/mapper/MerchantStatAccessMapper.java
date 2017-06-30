package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.MerchantStatAccess;
import com.treefinance.saas.monitor.dao.entity.MerchantStatAccessCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface MerchantStatAccessMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_access
     *
     * @mbg.generated Fri Jun 30 14:48:57 CST 2017
     */
    long countByExample(MerchantStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_access
     *
     * @mbg.generated Fri Jun 30 14:48:57 CST 2017
     */
    int deleteByExample(MerchantStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_access
     *
     * @mbg.generated Fri Jun 30 14:48:57 CST 2017
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_access
     *
     * @mbg.generated Fri Jun 30 14:48:57 CST 2017
     */
    int insert(MerchantStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_access
     *
     * @mbg.generated Fri Jun 30 14:48:57 CST 2017
     */
    int insertSelective(MerchantStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_access
     *
     * @mbg.generated Fri Jun 30 14:48:57 CST 2017
     */
    List<MerchantStatAccess> selectByExampleWithRowbounds(MerchantStatAccessCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_access
     *
     * @mbg.generated Fri Jun 30 14:48:57 CST 2017
     */
    List<MerchantStatAccess> selectByExample(MerchantStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_access
     *
     * @mbg.generated Fri Jun 30 14:48:57 CST 2017
     */
    List<MerchantStatAccess> selectPaginationByExample(MerchantStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_access
     *
     * @mbg.generated Fri Jun 30 14:48:57 CST 2017
     */
    int insertOrUpdateBySelective(MerchantStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_access
     *
     * @mbg.generated Fri Jun 30 14:48:57 CST 2017
     */
    void batchInsert(List<MerchantStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_access
     *
     * @mbg.generated Fri Jun 30 14:48:57 CST 2017
     */
    void batchUpdateByPrimaryKey(List<MerchantStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_access
     *
     * @mbg.generated Fri Jun 30 14:48:57 CST 2017
     */
    void batchUpdateByPrimaryKeySelective(List<MerchantStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_access
     *
     * @mbg.generated Fri Jun 30 14:48:57 CST 2017
     */
    MerchantStatAccess selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_access
     *
     * @mbg.generated Fri Jun 30 14:48:57 CST 2017
     */
    int updateByExampleSelective(@Param("record") MerchantStatAccess record, @Param("example") MerchantStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_access
     *
     * @mbg.generated Fri Jun 30 14:48:57 CST 2017
     */
    int updateByExample(@Param("record") MerchantStatAccess record, @Param("example") MerchantStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_access
     *
     * @mbg.generated Fri Jun 30 14:48:57 CST 2017
     */
    int updateByPrimaryKeySelective(MerchantStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_access
     *
     * @mbg.generated Fri Jun 30 14:48:57 CST 2017
     */
    int updateByPrimaryKey(MerchantStatAccess record);
}