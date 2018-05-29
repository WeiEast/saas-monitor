package com.treefinance.saas.monitor.facade.service.autostat;

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
     * 列表查询所有的basicData(分页)
     *
     * @return List<BasicDataRO>
     */
    MonitorResult<List<BasicDataRO>> queryAllBasicData(PageRequest pageRequest);


    /**
     * 新增basicData
     *
     * @param basicDataRequest
     * @return
     */
    MonitorResult<Boolean> addBasicData(BasicDataRequest basicDataRequest);

    /**
     * 更新basicData
     *
     * @param basicDataRequest
     * @return
     */
    MonitorResult<Boolean> updateBasicData(BasicDataRequest basicDataRequest);


    /**
     * 获取所有的基础数据名字
     *
     * @param
     * @return
     */
    MonitorResult<List<String>> queryAllDataName(BaseRequest baseRequest);


    /**
     * 根据ID获取基础数据
     *
     * @param basicDataRequest
     * @return BasicDataRO
     */
    MonitorResult<BasicDataRO> getBasicDataById(BasicDataRequest basicDataRequest);


}
