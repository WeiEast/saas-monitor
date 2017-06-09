package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.MerchantStatMail;
import com.treefinance.saas.monitor.dao.entity.MerchantStatMailCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface MerchantStatMailMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_mail
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    int countByExample(MerchantStatMailCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_mail
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    int deleteByExample(MerchantStatMailCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_mail
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_mail
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    int insert(MerchantStatMail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_mail
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    int insertSelective(MerchantStatMail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_mail
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    List<MerchantStatMail> selectByExampleWithRowbounds(MerchantStatMailCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_mail
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    List<MerchantStatMail> selectByExample(MerchantStatMailCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_mail
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    MerchantStatMail selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_mail
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    int updateByExampleSelective(@Param("record") MerchantStatMail record, @Param("example") MerchantStatMailCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_mail
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    int updateByExample(@Param("record") MerchantStatMail record, @Param("example") MerchantStatMailCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_mail
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    int updateByPrimaryKeySelective(MerchantStatMail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_mail
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    int updateByPrimaryKey(MerchantStatMail record);
}