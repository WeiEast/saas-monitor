package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.OperatorService;
import com.treefinance.saas.monitor.biz.service.WebsiteService;
import com.treefinance.saas.monitor.common.domain.dto.OperatorDTO;
import com.treefinance.saas.monitor.common.domain.dto.WebsiteDTO;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.Operator;
import com.treefinance.saas.monitor.dao.entity.OperatorCriteria;
import com.treefinance.saas.monitor.dao.mapper.OperatorMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/8.
 */
@Service("operatorService")
public class OperatorServiceImpl implements OperatorService {
    @Autowired
    private OperatorMapper operatorMapper;
    @Autowired
    private WebsiteService websiteService;

    @Override
    public List<OperatorDTO> getAll() {
        OperatorCriteria criteria = new OperatorCriteria();
        criteria.createCriteria();
        List<Operator> list = operatorMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return DataConverterUtils.convert(list, OperatorDTO.class);
    }

    @Override
    public OperatorDTO getOperatorByWebsiteId(Integer websiteId) {
        OperatorCriteria criteria = new OperatorCriteria();
        criteria.createCriteria().andWebsiteIdEqualTo(websiteId);
        List<Operator> list = operatorMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return DataConverterUtils.convert(list.get(0), OperatorDTO.class);
    }

    @Override
    public OperatorDTO getOperatorByWebsite(String website) {
        WebsiteDTO websiteDTO = websiteService.getWebsiteByName(website);
        if (websiteDTO == null) {
            return null;
        }
        return getOperatorByWebsiteId(websiteDTO.getId());
    }
}
