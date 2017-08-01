package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.WebsiteDTO;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/8.
 */
public interface WebsiteService {
    /**
     * 获取站点名称
     *
     * @param websiteName
     * @return
     */
    WebsiteDTO getWebsiteByName(String websiteName);

    /**
     * 获取支持的邮件列表
     *
     * @return
     */
    List<WebsiteDTO> getSupportMails();

    /**
     * 批量获取站点名称
     *
     * @param websites
     * @return
     */
    List<WebsiteDTO> getWebsiteListByName(List<String> websites);
}
