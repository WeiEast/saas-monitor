package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.ApiStatAccess;
import com.treefinance.saas.monitor.dao.entity.ApiStatAccessCriteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface ApiStatAccessMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    long countByExample(ApiStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int deleteByExample(ApiStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int insert(ApiStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int insertSelective(ApiStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    List<ApiStatAccess> selectByExampleWithRowbounds(ApiStatAccessCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    List<ApiStatAccess> selectByExample(ApiStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    List<ApiStatAccess> selectPaginationByExample(ApiStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int insertOrUpdateBySelective(ApiStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    void batchInsert(List<ApiStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    void batchUpdateByPrimaryKey(List<ApiStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    void batchUpdateByPrimaryKeySelective(List<ApiStatAccess> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    ApiStatAccess selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int updateByExampleSelective(@Param("record") ApiStatAccess record, @Param("example") ApiStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int updateByExample(@Param("record") ApiStatAccess record, @Param("example") ApiStatAccessCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int updateByPrimaryKeySelective(ApiStatAccess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_stat_access
     *
     * @mbg.generated Fri Jul 07 16:29:56 CST 2017
     */
    int updateByPrimaryKey(ApiStatAccess record);
}