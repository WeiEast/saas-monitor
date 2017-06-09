package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.WebsiteService;
import com.treefinance.saas.monitor.common.domain.dto.WebsiteDTO;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.Website;
import com.treefinance.saas.monitor.dao.entity.WebsiteCriteria;
import com.treefinance.saas.monitor.dao.mapper.WebsiteMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/8.
 */
@Service("websiteService")
public class WebsiteServiceImpl implements WebsiteService {
    @Autowired
    private WebsiteMapper websiteMapper;

    @Override
    public WebsiteDTO getWebsiteByName(String websiteName) {
        WebsiteCriteria criteria = new WebsiteCriteria();
        criteria.createCriteria().andWebsiteNameEqualTo(websiteName);
        List<Website> list = websiteMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return DataConverterUtils.convert(list.get(0), WebsiteDTO.class);
    }
}
