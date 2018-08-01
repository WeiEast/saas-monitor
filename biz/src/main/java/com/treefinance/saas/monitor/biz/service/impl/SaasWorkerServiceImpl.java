package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.SaasWorkerService;
import com.treefinance.saas.monitor.dao.entity.SaasWorker;
import com.treefinance.saas.monitor.dao.entity.SaasWorkerCriteria;
import com.treefinance.saas.monitor.dao.mapper.SaasWorkerMapper;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author chengtong
 * @date 18/5/28 14:56
 */
@Service
public class SaasWorkerServiceImpl implements SaasWorkerService {

    private static final Logger logger = LoggerFactory.getLogger(SaasWorkerServiceImpl.class);

    @Autowired
    private SaasWorkerMapper saasWorkerMapper;


    @Override
    public List<SaasWorker> getDutyWorker(Date baseTime) {
        return getOnDutyWorker();
    }

    @Override
    public List<SaasWorker> getNowDateWorker(Date now) {
        List<SaasWorker> workers = saasWorkerMapper.selectByExample(null);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return getActiveWorkers(now, workers);
    }

    /**
     * 获取当前时刻值班人员
     *
     * @param now
     * @param workers
     * @return
     */
    public List<SaasWorker> getActiveWorkers(Date now, List<SaasWorker> workers) {
        List<SaasWorker> active = new ArrayList<>();
        for (SaasWorker worker : workers) {
            try {
                if (StringUtils.isEmpty(worker.getDutyCorn())) {
                    continue;
                }
                CronExpression cronExpression = new CronExpression(worker.getDutyCorn());
                cronExpression.setTimeZone(TimeZone.getDefault());
                if (cronExpression.isSatisfiedBy(now)) {
                    active.add(worker);
                }
            } catch (ParseException e) {
                logger.error("工作人员corn表达式错误，name：{}", worker.getDutyCorn());
            }
        }
        return active;
    }

    @Override
    public List<SaasWorker> getAllSaasWorker() {
        return saasWorkerMapper.selectByExample(null);
    }

    private List<SaasWorker> getOnDutyWorker() {
        List<SaasWorker> workers = saasWorkerMapper.selectByExample(null);
        List<SaasWorker> active = new ArrayList<>();
        for (SaasWorker worker : workers) {
            try {
                if (StringUtils.isEmpty(worker.getDutyCorn())) {
                    continue;
                }
                CronExpression cronExpression = new CronExpression(worker.getDutyCorn());
                cronExpression.setTimeZone(TimeZone.getDefault());
                if (cronExpression.isSatisfiedBy(new Date())) {
                    active.add(worker);
                }
            } catch (ParseException e) {
                logger.error("工作人员corn表达式错误，name：{}", worker.getDutyCorn());
            }
        }
        return active;
    }

    @Override
    public SaasWorker getWorkerByName(String name) {

        SaasWorkerCriteria criteria = new SaasWorkerCriteria();
        criteria.createCriteria().andNameEqualTo(name);
        List<SaasWorker> list = saasWorkerMapper.selectByExample(criteria);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }


    @Override
    public List<SaasWorker> queryPaginateByCondition(SaasWorkerCriteria criteria) {
        return saasWorkerMapper.selectByExample(criteria);
    }

    @Override
    public long countByCondition(SaasWorkerCriteria criteria) {
        return saasWorkerMapper.countByExample(criteria);
    }
}
