package com.treefinance.saas.monitor.biz.autostat.elasticjob.impl;

import com.dangdang.ddframe.job.api.JobType;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.internal.config.LiteJobConfigurationGsonFactory;
import com.dangdang.ddframe.job.lite.internal.storage.JobNodePath;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.treefinance.saas.monitor.biz.autostat.elasticjob.ElasticSimpleJobService;
import com.treefinance.saas.monitor.biz.autostat.model.JobSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/1/19.
 */
@Service
public class ElasticSimpleJobServiceImpl implements ElasticSimpleJobService, ApplicationContextAware {
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ElasticSimpleJobServiceImpl.class);

    /**
     * spring context
     */
    private ApplicationContext applicationContext;

    /**
     * 注册中心
     */
    private CoordinatorRegistryCenter coordinatorRegistryCenter;

    /**
     * 创建任务
     *
     * @param simpleJob
     * @param jobCoreConfiguration
     */
    @Override
    public void createJob(SimpleJob simpleJob, JobCoreConfiguration jobCoreConfiguration) {
        String jobName = jobCoreConfiguration.getJobName();
        // 1.beanFactory
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        // 2.job event config
        SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(jobCoreConfiguration, simpleJob.getClass().getCanonicalName());
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfig).overwrite(true).build();

        // 4.bean definition
        List<BeanDefinition> result = new ManagedList<>(2);
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
        // todo  调用init方法,会使作业立马开始调度,导致同时更新多张相同的表,数据库死锁
        beanDefinitionBuilder.setInitMethodName("init");
        beanDefinitionBuilder.addConstructorArgValue(simpleJob);
        beanDefinitionBuilder.addConstructorArgValue(coordinatorRegistryCenter);
        beanDefinitionBuilder.addConstructorArgValue(liteJobConfiguration);
        beanDefinitionBuilder.addConstructorArgValue(result);
        beanDefinitionBuilder.setLazyInit(false);
        beanFactory.registerBeanDefinition(jobName, beanDefinitionBuilder.getBeanDefinition());

        // 创建Spring 任务
        logger.info("create job success : jobName={}, instance={}", jobName, beanFactory.getBean(jobName));
    }

    @Override
    public void triggerJob(String jobName) {
        JobNodePath jobNodePath = new JobNodePath(jobName);
        for (String each : coordinatorRegistryCenter.getChildrenKeys(jobNodePath.getInstancesNodePath())) {
            coordinatorRegistryCenter.persist(jobNodePath.getInstanceNodePath(each), "TRIGGER");
        }
    }

    @Override
    public void disableJob(String jobName) {
        disableOrEnableJobs(jobName, true);
    }

    @Override
    public void enableJob(String jobName) {
        disableOrEnableJobs(jobName, false);
    }

    @Override
    public void removeJob(String jobName) {
        shutdown(jobName);
        JobNodePath jobNodePath = new JobNodePath(jobName);
        List<String> servers = coordinatorRegistryCenter.getChildrenKeys(jobNodePath.getServerNodePath());
        for (String each : servers) {
            coordinatorRegistryCenter.remove(jobNodePath.getServerNodePath(each));
        }
    }

    @Override
    public void shutdown(String jobName) {
        JobNodePath jobNodePath = new JobNodePath(jobName);
        for (String each : coordinatorRegistryCenter.getChildrenKeys(jobNodePath.getInstancesNodePath())) {
            coordinatorRegistryCenter.remove(jobNodePath.getInstanceNodePath(each));
        }
    }

    /**
     * 禁用启用任务
     *
     * @param jobName
     * @param disabled
     */
    private void disableOrEnableJobs(String jobName, final boolean disabled) {
        JobNodePath jobNodePath = new JobNodePath(jobName);
        for (String each : coordinatorRegistryCenter.getChildrenKeys(jobNodePath.getServerNodePath())) {
            if (disabled) {
                coordinatorRegistryCenter.persist(jobNodePath.getServerNodePath(each), "DISABLED");
            } else {
                coordinatorRegistryCenter.persist(jobNodePath.getServerNodePath(each), "");
            }
        }
    }

    @Override
    public List<String> getAllJobNames() {
        List<String> jobNames = coordinatorRegistryCenter.getChildrenKeys("/");
        return jobNames;
    }

    @Override
    public boolean exists(String jobName) {
        JobNodePath jobNodePath = new JobNodePath(jobName);
        String liteJobConfigJson = coordinatorRegistryCenter.get(jobNodePath.getConfigNodePath());
        if (liteJobConfigJson == null) {
            return false;
        }
        return true;
    }

    @Override
    public JobSettings getJobSettings(String jobName) {
        JobSettings jobSettings = new JobSettings();
        JobNodePath jobNodePath = new JobNodePath(jobName);
        String liteJobConfigJson = coordinatorRegistryCenter.get(jobNodePath.getConfigNodePath());
        if (StringUtils.isEmpty(liteJobConfigJson)) {
            return jobSettings;
        }
        LiteJobConfiguration liteJobConfig = LiteJobConfigurationGsonFactory.fromJson(liteJobConfigJson);

        jobSettings.setJobName(jobName);
        jobSettings.setJobType(liteJobConfig.getTypeConfig().getJobType().name());
        jobSettings.setJobClass(liteJobConfig.getTypeConfig().getJobClass());
        jobSettings.setShardingTotalCount(liteJobConfig.getTypeConfig().getCoreConfig().getShardingTotalCount());
        jobSettings.setCron(liteJobConfig.getTypeConfig().getCoreConfig().getCron());
        jobSettings.setShardingItemParameters(liteJobConfig.getTypeConfig().getCoreConfig().getShardingItemParameters());
        jobSettings.setJobParameter(liteJobConfig.getTypeConfig().getCoreConfig().getJobParameter());
        jobSettings.setMonitorExecution(liteJobConfig.isMonitorExecution());
        jobSettings.setMaxTimeDiffSeconds(liteJobConfig.getMaxTimeDiffSeconds());
        jobSettings.setMonitorPort(liteJobConfig.getMonitorPort());
        jobSettings.setFailover(liteJobConfig.getTypeConfig().getCoreConfig().isFailover());
        jobSettings.setMisfire(liteJobConfig.getTypeConfig().getCoreConfig().isMisfire());
        jobSettings.setJobShardingStrategyClass(liteJobConfig.getJobShardingStrategyClass());
        jobSettings.setDescription(liteJobConfig.getTypeConfig().getCoreConfig().getDescription());
        jobSettings.setReconcileIntervalMinutes(liteJobConfig.getReconcileIntervalMinutes());
        jobSettings.getJobProperties().put(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(),
                liteJobConfig.getTypeConfig().getCoreConfig().getJobProperties().get(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER));
        jobSettings.getJobProperties().put(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(),
                liteJobConfig.getTypeConfig().getCoreConfig().getJobProperties().get(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER));

        String jobType = liteJobConfig.getTypeConfig().getJobType().name();
        if (JobType.DATAFLOW.name().equals(jobType)) {
            jobSettings.setStreamingProcess(((DataflowJobConfiguration) liteJobConfig.getTypeConfig()).isStreamingProcess());
        }
        if (JobType.SCRIPT.name().equals(jobType)) {
            jobSettings.setScriptCommandLine(((ScriptJobConfiguration) liteJobConfig.getTypeConfig()).getScriptCommandLine());
        }
        return jobSettings;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.coordinatorRegistryCenter = applicationContext.getBean(CoordinatorRegistryCenter.class);
    }

}
