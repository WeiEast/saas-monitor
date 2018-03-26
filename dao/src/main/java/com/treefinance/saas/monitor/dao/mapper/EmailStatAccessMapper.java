package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.EmailStatAccess;
import com.treefinance.saas.monitor.dao.entity.EmailStatAccessCriteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface EmailStatAccessMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table email_stat_access
     *
     * @mbg.generated Fri Mar 09 16:00:22 CST 2018
     */
    long countByExample(EmailStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table email_stat_access
     *
     * @mbg.generated Fri Mar 09 16:00:22 CST 2018
     */
    int deleteByExample(EmailStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table email_stat_access
     *
     * @mbg.generated Fri Mar 09 16:00:22 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table email_stat_access
     *
     * @mbg.generated Fri Mar 09 16:00:22 CST 2018
     */
    int insert(EmailStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table email_stat_access
     *
     * @mbg.generated Fri Mar 09 16:00:22 CST 2018
     */
    int insertSelective(EmailStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table email_stat_access
     *
     * @mbg.generated Fri Mar 09 16:00:22 CST 2018
     */
    List<EmailStatAccess> selectByExampleWithRowbounds(EmailStatAccessCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table email_stat_access
     *
     * @mbg.generated Fri Mar 09 16:00:22 CST 2018
     */
    List<EmailStatAccess> selectByExample(EmailStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table email_stat_access
     *
     * @mbg.generated Fri Mar 09 16:00:22 CST 2018
     */
    List<EmailStatAccess> selectPaginationByExample(EmailStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table email_stat_access
     *
     * @mbg.generated Fri Mar 09 16:00:22 CST 2018
     */
    int insertOrUpdateBySelective(EmailStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table email_stat_access
     *
     * @mbg.generated Fri Mar 09 16:00:22 CST 2018
     */
    void batchInsert(List<EmailStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table email_stat_access
     *
     * @mbg.generated Fri Mar 09 16:00:22 CST 2018
     */
    void batchUpdateByPrimaryKey(List<EmailStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table email_stat_access
     *
     * @mbg.generated Fri Mar 09 16:00:22 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<EmailStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table email_stat_access
     *
     * @mbg.generated Fri Mar 09 16:00:22 CST 2018
     */
    EmailStatAccess selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table email_stat_access
     *
     * @mbg.generated Fri Mar 09 16:00:22 CST 2018
     */
    int updateByExampleSelective(@Param("record") EmailStatAccess record, @Param("example") EmailStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table email_stat_access
     *
     * @mbg.generated Fri Mar 09 16:00:22 CST 2018
     */
    int updateByExample(@Param("record") EmailStatAccess record, @Param("example") EmailStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table email_stat_access
     *
     * @mbg.generated Fri Mar 09 16:00:22 CST 2018
     */
    int updateByPrimaryKeySelective(EmailStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table email_stat_access
     *
     * @mbg.generated Fri Mar 09 16:00:22 CST 2018
     */
    int updateByPrimaryKey(EmailStatAccess record);
}