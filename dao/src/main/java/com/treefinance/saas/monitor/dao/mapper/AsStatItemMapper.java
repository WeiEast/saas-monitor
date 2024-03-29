package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.AsStatItem;
import com.treefinance.saas.monitor.dao.entity.AsStatItemCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AsStatItemMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Mon May 21 17:34:07 CST 2018
     */
    long countByExample(AsStatItemCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Mon May 21 17:34:07 CST 2018
     */
    int deleteByExample(AsStatItemCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Mon May 21 17:34:07 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Mon May 21 17:34:07 CST 2018
     */
    int insert(AsStatItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Mon May 21 17:34:07 CST 2018
     */
    int insertSelective(AsStatItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Mon May 21 17:34:07 CST 2018
     */
    List<AsStatItem> selectByExampleWithRowbounds(AsStatItemCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Mon May 21 17:34:07 CST 2018
     */
    List<AsStatItem> selectByExample(AsStatItemCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Mon May 21 17:34:07 CST 2018
     */
    List<AsStatItem> selectPaginationByExample(AsStatItemCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Mon May 21 17:34:07 CST 2018
     */
    int insertOrUpdateBySelective(AsStatItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Mon May 21 17:34:07 CST 2018
     */
    void batchInsert(List<AsStatItem> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Mon May 21 17:34:07 CST 2018
     */
    void batchUpdateByPrimaryKey(List<AsStatItem> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Mon May 21 17:34:07 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<AsStatItem> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Mon May 21 17:34:07 CST 2018
     */
    AsStatItem selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Mon May 21 17:34:07 CST 2018
     */
    int updateByExampleSelective(@Param("record") AsStatItem record, @Param("example") AsStatItemCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Mon May 21 17:34:07 CST 2018
     */
    int updateByExample(@Param("record") AsStatItem record, @Param("example") AsStatItemCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Mon May 21 17:34:07 CST 2018
     */
    int updateByPrimaryKeySelective(AsStatItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_stat_item
     *
     * @mbg.generated Mon May 21 17:34:07 CST 2018
     */
    int updateByPrimaryKey(AsStatItem record);
}