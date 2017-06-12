package com.treefinance.saas.monitor.facade.service;

import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.MailRO;

import java.util.List;

/**
 * 邮箱查询服务
 * Created by yh-treefinance on 2017/6/12.
 */
public interface MailFacade {
    /**
     * 查询所有邮箱
     * @return
     */
    MonitorResult<List<MailRO>> queryAll();
}
