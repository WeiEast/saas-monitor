package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.StatItem;
import com.treefinance.saas.monitor.dao.entity.StatItemCriteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface StatItemMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    long countByExample(StatItemCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int deleteByExample(StatItemCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int insert(StatItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int insertSelective(StatItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    List<StatItem> selectByExampleWithRowbounds(StatItemCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    List<StatItem> selectByExample(StatItemCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    List<StatItem> selectPaginationByExample(StatItemCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int insertOrUpdateBySelective(StatItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    void batchInsert(List<StatItem> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    void batchUpdateByPrimaryKey(List<StatItem> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<StatItem> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    StatItem selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int updateByExampleSelective(@Param("record") StatItem record, @Param("example") StatItemCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int updateByExample(@Param("record") StatItem record, @Param("example") StatItemCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int updateByPrimaryKeySelective(StatItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int updateByPrimaryKey(StatItem record);
}