package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.dao.entity.SaasWorker;

import java.util.Date;

/**
 * @author chengtong
 * @date 18/5/28 14:55
 */
public interface SaasWorkerService {

    SaasWorker getDutyWorker(Date baseTime);

}
