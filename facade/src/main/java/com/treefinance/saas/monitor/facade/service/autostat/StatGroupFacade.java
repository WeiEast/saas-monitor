package com.treefinance.saas.monitor.facade.service.autostat;

import com.treefinance.saas.monitor.facade.domain.base.BaseRequest;
import com.treefinance.saas.monitor.facade.domain.request.StatGroupRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.stat.StatGroupRO;

import java.util.List;
import java.util.Set;

/**
 * @author:guoguoyun
 * @date:Created in 2018/4/26下午7:46
 */
public interface StatGroupFacade {

    /**
     * 返回统计分组列表
     * @param groupStatRequest templateId/groupIndex;
     * @return List<StatGroupRO>
     */
    MonitorResult<List<StatGroupRO>> queryStatGroup(StatGroupRequest groupStatRequest);


    /**
     * 新增或修改统计分组
     * @param groupStatRequest
     * @return
     */
    MonitorResult<Boolean>  addOrUpdateStatGroup(StatGroupRequest groupStatRequest);


    /**
     * 返回所有的分组序号
     * @param baseRequest
     * @return List<Integer>
     */
    MonitorResult<Set<Integer>>  queryAllGroupIndex(BaseRequest baseRequest);

}
