package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.SaasWorker;
import com.treefinance.saas.monitor.dao.entity.SaasWorkerCriteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface SaasWorkerMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_worker
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    long countByExample(SaasWorkerCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_worker
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int deleteByExample(SaasWorkerCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_worker
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_worker
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int insert(SaasWorker record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_worker
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int insertSelective(SaasWorker record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_worker
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    List<SaasWorker> selectByExampleWithRowbounds(SaasWorkerCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_worker
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    List<SaasWorker> selectByExample(SaasWorkerCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_worker
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    List<SaasWorker> selectPaginationByExample(SaasWorkerCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_worker
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int insertOrUpdateBySelective(SaasWorker record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_worker
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    void batchInsert(List<SaasWorker> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_worker
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    void batchUpdateByPrimaryKey(List<SaasWorker> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_worker
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<SaasWorker> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_worker
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    SaasWorker selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_worker
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int updateByExampleSelective(@Param("record") SaasWorker record, @Param("example") SaasWorkerCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_worker
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int updateByExample(@Param("record") SaasWorker record, @Param("example") SaasWorkerCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_worker
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int updateByPrimaryKeySelective(SaasWorker record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_worker
     *
     * @mbg.generated Tue May 29 17:39:33 CST 2018
     */
    int updateByPrimaryKey(SaasWorker record);
}