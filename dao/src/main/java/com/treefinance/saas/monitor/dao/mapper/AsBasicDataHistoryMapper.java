package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.AsBasicDataHistory;
import com.treefinance.saas.monitor.dao.entity.AsBasicDataHistoryCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AsBasicDataHistoryMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_basic_data_history
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    long countByExample(AsBasicDataHistoryCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_basic_data_history
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int deleteByExample(AsBasicDataHistoryCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_basic_data_history
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_basic_data_history
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int insert(AsBasicDataHistory record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_basic_data_history
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int insertSelective(AsBasicDataHistory record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_basic_data_history
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    List<AsBasicDataHistory> selectByExampleWithRowbounds(AsBasicDataHistoryCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_basic_data_history
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    List<AsBasicDataHistory> selectByExample(AsBasicDataHistoryCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_basic_data_history
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    List<AsBasicDataHistory> selectPaginationByExample(AsBasicDataHistoryCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_basic_data_history
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int insertOrUpdateBySelective(AsBasicDataHistory record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_basic_data_history
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    void batchInsert(List<AsBasicDataHistory> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_basic_data_history
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    void batchUpdateByPrimaryKey(List<AsBasicDataHistory> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_basic_data_history
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<AsBasicDataHistory> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_basic_data_history
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    AsBasicDataHistory selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_basic_data_history
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int updateByExampleSelective(@Param("record") AsBasicDataHistory record, @Param("example") AsBasicDataHistoryCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_basic_data_history
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int updateByExample(@Param("record") AsBasicDataHistory record, @Param("example") AsBasicDataHistoryCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_basic_data_history
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int updateByPrimaryKeySelective(AsBasicDataHistory record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_basic_data_history
     *
     * @mbg.generated Fri Apr 27 17:01:41 CST 2018
     */
    int updateByPrimaryKey(AsBasicDataHistory record);
}