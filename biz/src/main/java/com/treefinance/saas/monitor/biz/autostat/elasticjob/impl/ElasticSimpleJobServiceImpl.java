package com.treefinance.saas.monitor.biz.autostat.elasticjob.impl;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.internal.storage.JobNodePath;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.treefinance.saas.monitor.biz.autostat.elasticjob.ElasticSimpleJobService;
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
        beanDefinitionBuilder.setInitMethodName("init");
        beanDefinitionBuilder.addConstructorArgValue(simpleJob);
        beanDefinitionBuilder.addConstructorArgValue(coordinatorRegistryCenter);
        beanDefinitionBuilder.addConstructorArgValue(liteJobConfiguration);
        beanDefinitionBuilder.addConstructorArgValue(result);
        beanFactory.registerBeanDefinition(jobName, beanDefinitionBuilder.getBeanDefinition());

        // 创建Spring 任务
        logger.info("create job success : " + beanFactory.getBean(jobName));
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
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.coordinatorRegistryCenter = applicationContext.getBean(CoordinatorRegistryCenter.class);
    }

}
