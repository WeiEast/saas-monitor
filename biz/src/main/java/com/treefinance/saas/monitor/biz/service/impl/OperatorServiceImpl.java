package com.treefinance.saas.monitor.biz.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.service.OperatorService;
import com.treefinance.saas.monitor.biz.service.WebsiteService;
import com.treefinance.saas.monitor.common.domain.dto.OperatorDTO;
import com.treefinance.saas.monitor.common.domain.dto.WebsiteDTO;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.Operator;
import com.treefinance.saas.monitor.dao.entity.OperatorCriteria;
import com.treefinance.saas.monitor.dao.mapper.OperatorMapper;
import com.treefinance.saas.monitor.facade.domain.ro.OperatorRO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public Map<String, OperatorRO> getOperatorByWebsites(List<String> websites) {
        Map<String, OperatorRO> resultMap = Maps.newHashMap();
        List<WebsiteDTO> websiteDTOList = websiteService.getWebsiteListByName(websites);
        if (CollectionUtils.isEmpty(websiteDTOList)) {
            return resultMap;
        }
        Map<String, Integer> websiteNameIdMap = websiteDTOList.stream().collect(Collectors.toMap(WebsiteDTO::getWebsiteName, WebsiteDTO::getId));
        List<Integer> websiteIdList = websiteDTOList.stream().map(WebsiteDTO::getId).collect(Collectors.toList());
        List<OperatorDTO> operatorDTOList = getOperatorListByWebsiteIds(websiteIdList);
        Map<Integer, OperatorDTO> operatorDTOMap = operatorDTOList.stream().collect(Collectors.toMap(OperatorDTO::getWebsiteId, operatorDTO -> operatorDTO));
        for (Map.Entry<String, Integer> entry : websiteNameIdMap.entrySet()) {
            OperatorDTO operatorDTO = operatorDTOMap.get(entry.getValue());
            if (operatorDTO != null) {
                OperatorRO operatorRO = DataConverterUtils.convert(operatorDTO, OperatorRO.class);
                resultMap.put(entry.getKey(), operatorRO);
            }
        }
        return resultMap;
    }

    @Override
    public List<OperatorDTO> getOperatorListByWebsiteIds(List<Integer> websiteIds) {
        OperatorCriteria criteria = new OperatorCriteria();
        criteria.createCriteria().andWebsiteIdIn(websiteIds);
        List<Operator> list = operatorMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        return DataConverterUtils.convert(list, OperatorDTO.class);
    }
}
