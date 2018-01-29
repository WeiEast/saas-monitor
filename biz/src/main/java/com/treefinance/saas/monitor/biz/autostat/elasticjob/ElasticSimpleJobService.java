package com.treefinance.saas.monitor.biz.autostat.elasticjob;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;

/**
 * Created by yh-treefinance on 2018/1/19.
 */
public interface ElasticSimpleJobService {
    /**
     * 创建任务
     *
     * @param simpleJob
     * @param jobCoreConfiguration
     */
    void createJob(SimpleJob simpleJob, JobCoreConfiguration jobCoreConfiguration);

    /**
     * 触发任务
     *
     * @param jobName 任务名称
     */
    void triggerJob(String jobName);

    /**
     * 禁用任务
     *
     * @param jobName
     */
    void disableJob(String jobName);

    /**
     * 启用任务
     *
     * @param jobName
     */
    void enableJob(String jobName);

    /**
     * 删除任务
     *
     * @param jobName
     */
    void removeJob(String jobName);

    /**
     * 停止任务
     *
     * @param jobName
     */
    void shutdown(String jobName);
}
