package com.treefinance.saas.monitor.facade.service;

import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.WebsiteRO;

import java.util.List;

/**
 * 根据website查询具体信息接口
 * Created by haojiahong on 2017/8/15.
 */
public interface WebsiteFacade {

    /**
     * 根据t_task表中的website字段查询具体的运营商,邮箱,银行,电商信息
     *
     * @param stringList
     * @return
     */
    MonitorResult<List<WebsiteRO>> queryWebsiteDetailByWebsiteName(List<String> stringList);
}
