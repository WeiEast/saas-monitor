package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.WebsiteDTO;

/**
 * Created by yh-treefinance on 2017/6/8.
 */
public interface WebsiteService {
    /**
     * 获取站点名称
     * @param websiteName
     * @return
     */
    public WebsiteDTO getWebsiteByName(String websiteName);
}
