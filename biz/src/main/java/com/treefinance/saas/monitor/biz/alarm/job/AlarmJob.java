package com.treefinance.saas.monitor.biz.alarm.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmConfig;
import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;

/**
 * Created by yh-treefinance on 2018/7/18.
 */
public class AlarmJob implements SimpleJob {

    /**
     * 当前环境
     */
    private ESaasEnv saasEnv = ESaasEnv.getByValue(Integer.valueOf(Constants.SAAS_ENV_VALUE));


    @Override
    public void execute(ShardingContext shardingContext) {
        AlarmConfig alarmConfig = new AlarmConfig();


    }
}
