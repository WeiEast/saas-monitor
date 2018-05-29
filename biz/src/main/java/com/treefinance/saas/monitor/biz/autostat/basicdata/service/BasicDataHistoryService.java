package com.treefinance.saas.monitor.biz.autostat.basicdata.service;

import com.treefinance.saas.monitor.facade.domain.request.autostat.BasicDataHistoryRequest;
import com.treefinance.saas.monitor.facade.domain.ro.autostat.BasicDataHistoryRO;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/5/17.
 */
public interface BasicDataHistoryService {

    /**
     * 查询列表
     * @param request
     * @return
     */
    List<BasicDataHistoryRO> queryList(BasicDataHistoryRequest request);

    /**
     * 查询总数
     *
     * @param request
     * @return
     */
    long count(BasicDataHistoryRequest request);
}
