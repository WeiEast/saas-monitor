package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.SaasWorkerService;
import com.treefinance.saas.monitor.dao.entity.SaasWorker;
import com.treefinance.saas.monitor.dao.mapper.SaasWorkerMapper;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author chengtong
 * @date 18/5/28 14:56
 */
@Service
public class SaasWorkerServiceImpl implements SaasWorkerService {


    @Autowired
    private SaasWorkerMapper saasWorkerMapper;

    public static SaasWorker saasWorker;

    @Override
    public SaasWorker getDutyWorker(Date baseTime) {
        return saasWorker;
    }


    @Scheduled(cron = "1 0 0 * * * ?")
    private void getDutyWorker(){

        List<SaasWorker> workers = saasWorkerMapper.selectByExample(null);
        for (SaasWorker worker:workers){
            try {
                CronExpression cronExpression = new CronExpression(worker.getDutyCorn());
                cronExpression.setTimeZone(TimeZone.getDefault());
                if(cronExpression.isSatisfiedBy(new Date())){
                    saasWorker = worker;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

}
