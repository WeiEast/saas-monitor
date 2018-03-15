package com.treefinance.saas.monitor.facade.service.stat;

import com.treefinance.saas.monitor.facade.domain.request.EmailStatAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.stat.email.EmailStatAccessBaseRO;

import java.util.List;

/**
 * @author chengtong
 * @date 18/3/15 09:45
 */
public interface EmailStatAccessFacade {

    /**
     * 返回时间段(日期)内的所有邮箱的监控数据
     * */
    MonitorResult<List<EmailStatAccessBaseRO>> queryEmailStatDayAccessList(EmailStatAccessRequest request);

    /**
     * 返回某一天内的所有邮箱的监控数据
     * */
    MonitorResult<List<EmailStatAccessBaseRO>> queryEmailStatDayAccessListDetail(EmailStatAccessRequest request);



}
