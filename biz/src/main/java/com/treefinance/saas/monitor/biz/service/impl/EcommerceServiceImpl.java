package com.treefinance.saas.monitor.biz.service.impl;

import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.service.EcommerceService;
import com.treefinance.saas.monitor.biz.service.WebsiteService;
import com.treefinance.saas.monitor.common.domain.dto.EcommerceDTO;
import com.treefinance.saas.monitor.common.domain.dto.WebsiteDTO;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.Ecommerce;
import com.treefinance.saas.monitor.dao.entity.EcommerceCriteria;
import com.treefinance.saas.monitor.dao.mapper.EcommerceMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/8.
 */
@Service("ecommerceService")
public class EcommerceServiceImpl implements EcommerceService {
    /**
     * logger
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private EcommerceMapper ecommerceMapper;
    @Autowired
    private WebsiteService websiteService;

    @Override
    public EcommerceDTO getEcommerceByWebsite(String website) {
        WebsiteDTO websiteDTO = websiteService.getWebsiteByName(website);
        if (websiteDTO == null) {
            return null;
        }
        EcommerceCriteria criteria = new EcommerceCriteria();
        criteria.createCriteria().andWebsiteIdEqualTo(websiteDTO.getId());
        List<Ecommerce> list = ecommerceMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return DataConverterUtils.convert(list.get(0), EcommerceDTO.class);
    }

    @Override
    public List<EcommerceDTO> getAll() {
        EcommerceCriteria criteria = new EcommerceCriteria();
        criteria.createCriteria();
        List<Ecommerce> list = ecommerceMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            logger.error("电商列表为空...");
            return null;
        }
        return DataConverterUtils.convert(list, EcommerceDTO.class);
    }
}
