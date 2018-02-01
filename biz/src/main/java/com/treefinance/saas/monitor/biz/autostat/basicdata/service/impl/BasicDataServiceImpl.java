package com.treefinance.saas.monitor.biz.autostat.basicdata.service.impl;

import com.treefinance.saas.monitor.biz.autostat.basicdata.service.BasicDataService;
import com.treefinance.saas.monitor.dao.entity.BasicData;
import com.treefinance.saas.monitor.dao.entity.BasicDataCriteria;
import com.treefinance.saas.monitor.dao.mapper.BasicDataMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/1/22.
 */
@Service
public class BasicDataServiceImpl implements BasicDataService {
    @Autowired
    private BasicDataMapper basicDataMapper;


    @Override
    public List<BasicData> getAll() {
        return basicDataMapper.selectByExample(new BasicDataCriteria());
    }

    @Override
    public BasicData getByCode(String dataCode) {
        BasicDataCriteria criteria = new BasicDataCriteria();
        criteria.createCriteria().andDataCodeEqualTo(dataCode);
        List<BasicData> list = basicDataMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public BasicData getById(Long id) {
        return basicDataMapper.selectByPrimaryKey(id);
    }
}
