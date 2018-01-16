package com.treefinance.saas.monitor.facade.service.stat;

import com.treefinance.saas.monitor.facade.domain.request.EcommerceDetailAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.stat.ecommerce.EcommerceAllDetailRO;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/1/15上午10:50
 */
public interface EcommerceStatDivisionAccessFacade {


    /**
     * 查询电商总分时监控统计数据
     *
     * @param request
     * @return
     */
    MonitorResult<List<EcommerceAllDetailRO>> queryEcommerceAllDetailAccessList(EcommerceDetailAccessRequest request);

//    /**
//     * 查询电商详细分时监控统计数据
//     *
//     * @param request
//     * @return
//     */
//    MonitorResult<List<EcommerceAllDetailRO>> queryEcommerceEachDetailAccessList(EcommerceDetailAccessRequest request);


}
