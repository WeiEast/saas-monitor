package com.treefinance.saas.monitor.biz.service.impl;

import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.service.WebsiteService;
import com.treefinance.saas.monitor.common.domain.dto.WebsiteDTO;
import com.treefinance.saas.monitor.context.component.AbstractService;
import com.treefinance.saas.monitor.dao.entity.Website;
import com.treefinance.saas.monitor.dao.entity.WebsiteCriteria;
import com.treefinance.saas.monitor.dao.mapper.WebsiteMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/8.
 */
@Service("websiteService")
public class WebsiteServiceImpl extends AbstractService implements WebsiteService {
    @Autowired
    private WebsiteMapper websiteMapper;

    @Override
    public WebsiteDTO getWebsiteByName(String websiteName) {
        if (StringUtils.isEmpty(websiteName)) {
            return null;
        }
        WebsiteCriteria criteria = new WebsiteCriteria();
        criteria.createCriteria().andWebsiteNameEqualTo(websiteName);
        List<Website> list = websiteMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return convert(list.get(0), WebsiteDTO.class);
    }

    @Override
    public List<WebsiteDTO> getSupportMails() {
        WebsiteCriteria criteria = new WebsiteCriteria();
        criteria.createCriteria().andWebsiteTypeEqualTo("1").andTemplateIdIsNotNull();
        List<Website> list = websiteMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return convert(list, WebsiteDTO.class);
    }

    @Override
    public List<WebsiteDTO> getWebsiteListByName(List<String> websites) {
        List<WebsiteDTO> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(websites)) {
            return result;
        }
        WebsiteCriteria criteria = new WebsiteCriteria();
        criteria.createCriteria().andWebsiteNameIn(websites);
        List<Website> list = websiteMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        return convert(list, WebsiteDTO.class);
    }
}
