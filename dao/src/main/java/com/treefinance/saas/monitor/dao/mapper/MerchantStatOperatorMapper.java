package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.MerchantStatOperator;
import com.treefinance.saas.monitor.dao.entity.MerchantStatOperatorCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface MerchantStatOperatorMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_operator
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    int countByExample(MerchantStatOperatorCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_operator
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    int deleteByExample(MerchantStatOperatorCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_operator
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_operator
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    int insert(MerchantStatOperator record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_operator
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    int insertSelective(MerchantStatOperator record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_operator
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    List<MerchantStatOperator> selectByExampleWithRowbounds(MerchantStatOperatorCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_operator
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    List<MerchantStatOperator> selectByExample(MerchantStatOperatorCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_operator
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    MerchantStatOperator selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_operator
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    int updateByExampleSelective(@Param("record") MerchantStatOperator record, @Param("example") MerchantStatOperatorCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_operator
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    int updateByExample(@Param("record") MerchantStatOperator record, @Param("example") MerchantStatOperatorCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_operator
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    int updateByPrimaryKeySelective(MerchantStatOperator record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_operator
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    int updateByPrimaryKey(MerchantStatOperator record);
}