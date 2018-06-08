package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.dao.entity.SaasWorker;

import java.util.Date;
import java.util.List;

/**
 * @author chengtong
 * @date 18/5/28 14:55
 */
public interface SaasWorkerService {

    List<SaasWorker> getDutyWorker(Date baseTime);

    List<SaasWorker> getAllSaasWorker();

    SaasWorker getWorkerByName(String name);

}
