

package com.treefinance.saas.monitor.biz.autostat.model;

import com.dangdang.ddframe.job.executor.handler.JobProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public final class JobSettings implements Serializable {

    private static final long serialVersionUID = -6532210090618686688L;

    private String jobName;

    private String jobType;

    private String jobClass;

    private String cron;

    private int shardingTotalCount;

    private String shardingItemParameters;

    private String jobParameter;

    private boolean monitorExecution;

    private boolean streamingProcess;

    private int maxTimeDiffSeconds;

    private int monitorPort = -1;

    private boolean failover;

    private boolean misfire;

    private String jobShardingStrategyClass;

    private String description;

    private Map<String, String> jobProperties = new LinkedHashMap<>(JobProperties.JobPropertiesEnum.values().length, 1);

    private String scriptCommandLine;

    private int reconcileIntervalMinutes;
}
