package com.treefinance.saas.monitor.facade.service.stat;

import com.treefinance.saas.monitor.facade.domain.request.OperatorStatAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.stat.operator.AllOperatorStatDayAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.operator.OperatorStatAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.operator.OperatorStatDayAccessRO;

import java.util.List;

/**
 * 运营商监控
 *
 * @author haojiahong
 * @date 2017/11/1
 */
public interface OperatorStatAccessFacade {


    /**
     * 查询各个运营商日监控统计数据
     *
     * @param request
     * @return
     */
    MonitorResult<List<OperatorStatDayAccessRO>> queryOperatorStatDayAccessList(OperatorStatAccessRequest request);

    /**
     * 查询各个运营商日监控统计数据(分页)
     *
     * @param request
     * @return
     */
    MonitorResult<List<OperatorStatDayAccessRO>> queryOperatorStatDayAccessListWithPage(OperatorStatAccessRequest request);

    /**
     * 查询某一个运营商日监控统计数据(分页),如:联通
     *
     * @param request
     * @return
     */
    MonitorResult<List<OperatorStatDayAccessRO>> queryOneOperatorStatDayAccessListWithPage(OperatorStatAccessRequest request);

    /**
     * 查询各个运营商小时监控统计数据
     *
     * @param request
     * @return
     */
    MonitorResult<List<OperatorStatAccessRO>> queryOperatorStatAccessList(OperatorStatAccessRequest request);

    /**
     * 查询所有运营商日监控统计数据
     *
     * @param request
     * @return
     */
    MonitorResult<List<AllOperatorStatDayAccessRO>> queryAllOperatorStatDayAccessList(OperatorStatAccessRequest request);

    /**
     * 查询所有运营商日监控统计数据(分页)
     *
     * @param request
     * @return
     */
    MonitorResult<List<AllOperatorStatDayAccessRO>> queryAllOperatorStatDayAccessListWithPage(OperatorStatAccessRequest request);


}
