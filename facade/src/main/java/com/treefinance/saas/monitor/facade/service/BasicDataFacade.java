package com.treefinance.saas.monitor.facade.service;

import com.treefinance.saas.monitor.facade.domain.base.BaseRequest;
import com.treefinance.saas.monitor.facade.domain.base.PageRequest;
import com.treefinance.saas.monitor.facade.domain.request.BasicDataRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.BasicDataRO;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/4/23下午5:21
 */
public interface BasicDataFacade {

    /**
     * 无条件列表查询所有的basicData
     * @return  List<BasicDataRO>
     */
    MonitorResult<List<BasicDataRO>> queryAllBasicData(PageRequest pageRequest);


    /**
     * 新增basicData
     * @param basicDataRequest
     * @return
     */

    int addBasicData(BasicDataRequest basicDataRequest);


}
