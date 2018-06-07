package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.SaasWorkerService;
import com.treefinance.saas.monitor.dao.entity.SaasWorker;
import com.treefinance.saas.monitor.dao.entity.SaasWorkerCriteria;
import com.treefinance.saas.monitor.dao.mapper.SaasWorkerMapper;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
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
        if(saasWorker == null){
            saasWorker = getOnDutyWorker();
        }
        return saasWorker;
    }

    @Override
    public List<SaasWorker> getAllSaasWorker() {
        return saasWorkerMapper.selectByExample(null);
    }

    @Scheduled(cron = "1 0 0 * * ?")
    private void getDutyWorker(){
        saasWorker = getOnDutyWorker();
    }

    private SaasWorker getOnDutyWorker() {
        List<SaasWorker> workers = saasWorkerMapper.selectByExample(null);
        SaasWorker active = null;
        for (SaasWorker worker:workers){
            try {
                CronExpression cronExpression = new CronExpression(worker.getDutyCorn());
                cronExpression.setTimeZone(TimeZone.getDefault());
                if(cronExpression.isSatisfiedBy(new Date())){
                    active = worker;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return active;
    }

    @Override
    public SaasWorker getWorkerByName(String name) {

        SaasWorkerCriteria criteria = new SaasWorkerCriteria();
        criteria.createCriteria().andNameEqualTo(name);
        List<SaasWorker> list = saasWorkerMapper.selectByExample(criteria);
        if(list == null|| list.isEmpty()){
            return null;
        }
        return list.get(0);
    }
}
