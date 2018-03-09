package com.treefinance.saas.monitor.biz.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.service.EmailMonitorAlarmService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: chengtong
 * @Date: 18/3/9 16:19
 */
public class EmailMonitorAlarmJob implements SimpleJob{


    @Autowired
    private DiamondConfig config;

    @Autowired
    private EmailMonitorAlarmService emailMonitorAlarmService;


    @Override
    public void execute(ShardingContext shardingContext) {



    }
}
