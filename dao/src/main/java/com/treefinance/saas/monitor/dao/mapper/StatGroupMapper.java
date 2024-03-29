package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.StatGroup;
import com.treefinance.saas.monitor.dao.entity.StatGroupCriteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface StatGroupMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_group
     *
     * @mbg.generated Fri Apr 27 15:28:02 CST 2018
     */
    long countByExample(StatGroupCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_group
     *
     * @mbg.generated Fri Apr 27 15:28:02 CST 2018
     */
    int deleteByExample(StatGroupCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_group
     *
     * @mbg.generated Fri Apr 27 15:28:02 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_group
     *
     * @mbg.generated Fri Apr 27 15:28:02 CST 2018
     */
    int insert(StatGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_group
     *
     * @mbg.generated Fri Apr 27 15:28:02 CST 2018
     */
    int insertSelective(StatGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_group
     *
     * @mbg.generated Fri Apr 27 15:28:02 CST 2018
     */
    List<StatGroup> selectByExampleWithRowbounds(StatGroupCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_group
     *
     * @mbg.generated Fri Apr 27 15:28:02 CST 2018
     */
    List<StatGroup> selectByExample(StatGroupCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_group
     *
     * @mbg.generated Fri Apr 27 15:28:02 CST 2018
     */
    List<StatGroup> selectPaginationByExample(StatGroupCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_group
     *
     * @mbg.generated Fri Apr 27 15:28:02 CST 2018
     */
    int insertOrUpdateBySelective(StatGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_group
     *
     * @mbg.generated Fri Apr 27 15:28:02 CST 2018
     */
    void batchInsert(List<StatGroup> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_group
     *
     * @mbg.generated Fri Apr 27 15:28:02 CST 2018
     */
    void batchUpdateByPrimaryKey(List<StatGroup> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_group
     *
     * @mbg.generated Fri Apr 27 15:28:02 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<StatGroup> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_group
     *
     * @mbg.generated Fri Apr 27 15:28:02 CST 2018
     */
    StatGroup selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_group
     *
     * @mbg.generated Fri Apr 27 15:28:02 CST 2018
     */
    int updateByExampleSelective(@Param("record") StatGroup record, @Param("example") StatGroupCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_group
     *
     * @mbg.generated Fri Apr 27 15:28:02 CST 2018
     */
    int updateByExample(@Param("record") StatGroup record, @Param("example") StatGroupCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_group
     *
     * @mbg.generated Fri Apr 27 15:28:02 CST 2018
     */
    int updateByPrimaryKeySelective(StatGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_group
     *
     * @mbg.generated Fri Apr 27 15:28:02 CST 2018
     */
    int updateByPrimaryKey(StatGroup record);
}