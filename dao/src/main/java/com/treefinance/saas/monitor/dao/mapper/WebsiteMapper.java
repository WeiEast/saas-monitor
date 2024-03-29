package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.Website;
import com.treefinance.saas.monitor.dao.entity.WebsiteCriteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface WebsiteMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_website
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    int countByExample(WebsiteCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_website
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    int deleteByExample(WebsiteCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_website
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    int insert(Website record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_website
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    int insertSelective(Website record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_website
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    List<Website> selectByExampleWithBLOBsWithRowbounds(WebsiteCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_website
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    List<Website> selectByExampleWithBLOBs(WebsiteCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_website
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    List<Website> selectByExampleWithRowbounds(WebsiteCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_website
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    List<Website> selectByExample(WebsiteCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_website
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    int updateByExampleSelective(@Param("record") Website record, @Param("example") WebsiteCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_website
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    int updateByExampleWithBLOBs(@Param("record") Website record, @Param("example") WebsiteCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_website
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    int updateByExample(@Param("record") Website record, @Param("example") WebsiteCriteria example);
}