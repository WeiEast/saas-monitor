package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.ApiStatTotalAccess;
import com.treefinance.saas.monitor.dao.entity.ApiStatTotalAccessCriteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface ApiStatTotalAccessMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_total_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    long countByExample(ApiStatTotalAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_total_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int deleteByExample(ApiStatTotalAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_total_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_total_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int insert(ApiStatTotalAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_total_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int insertSelective(ApiStatTotalAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_total_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    List<ApiStatTotalAccess> selectByExampleWithRowbounds(ApiStatTotalAccessCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_total_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    List<ApiStatTotalAccess> selectByExample(ApiStatTotalAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_total_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    List<ApiStatTotalAccess> selectPaginationByExample(ApiStatTotalAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_total_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int insertOrUpdateBySelective(ApiStatTotalAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_total_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    void batchInsert(List<ApiStatTotalAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_total_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    void batchUpdateByPrimaryKey(List<ApiStatTotalAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_total_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    void batchUpdateByPrimaryKeySelective(List<ApiStatTotalAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_total_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    ApiStatTotalAccess selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_total_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int updateByExampleSelective(@Param("record") ApiStatTotalAccess record, @Param("example") ApiStatTotalAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_total_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int updateByExample(@Param("record") ApiStatTotalAccess record, @Param("example") ApiStatTotalAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_total_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int updateByPrimaryKeySelective(ApiStatTotalAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_total_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int updateByPrimaryKey(ApiStatTotalAccess record);
}