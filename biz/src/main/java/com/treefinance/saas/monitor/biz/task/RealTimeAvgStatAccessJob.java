package com.treefinance.saas.monitor.biz.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.treefinance.saas.monitor.biz.service.RealTimeAvgStatAccessService;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Good Luck Bro , No Bug !
 * 每天计算实时监控统计数据前7天平均值,并存入redis中
 *
 * @author haojiahong
 * @date 2018/6/26
 */
public class RealTimeAvgStatAccessJob implements SimpleJob {

    private static final Logger logger = LoggerFactory.getLogger(RealTimeAvgStatAccessJob.class);

    @Autowired
    private RealTimeAvgStatAccessService realTimeAvgStatAccessService;

    @Override
    public void execute(ShardingContext shardingContext) {
        logger.info("任务实时监控,定时存储数据任务执行,now={}", MonitorDateUtils.format(new Date()));
        long startTime = System.currentTimeMillis();
        try {
            realTimeAvgStatAccessService.saveDataOnFixedTime();
        } catch (Exception e) {
            logger.info("任务实时监控,定时存储数据任务执行异常,now={}", MonitorDateUtils.format(new Date()), e);
        } finally {
            logger.info("任务实时监控,定时存储数据任务执行结束,耗时{}ms,now={}", System.currentTimeMillis() - startTime, MonitorDateUtils.format(new Date()));
        }
    }


}
