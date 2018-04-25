package com.treefinance.saas.monitor.biz.autostat.basicdata.service.impl;

import com.treefinance.saas.monitor.biz.autostat.basicdata.service.BasicDataService;
import com.treefinance.saas.monitor.dao.entity.BasicData;
import com.treefinance.saas.monitor.dao.entity.BasicDataCriteria;
import com.treefinance.saas.monitor.dao.mapper.BasicDataMapper;
import com.treefinance.saas.monitor.facade.domain.base.PageRequest;
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
    public List<BasicData> queryAll() {
        return basicDataMapper.selectByExample(new BasicDataCriteria());
    }

    @Override
    public List<BasicData> queryAllBasicDataWithPaging(PageRequest pageRequest){
        BasicDataCriteria basicDataCriteria = new BasicDataCriteria();
        basicDataCriteria.setLimit(pageRequest.getPageSize());
        basicDataCriteria.setOffset(pageRequest.getOffset());
        basicDataCriteria.createCriteria();

        return basicDataMapper.selectPaginationByExample(basicDataCriteria);

    }



    @Override
    public BasicData queryByCode(String dataCode) {
        BasicDataCriteria criteria = new BasicDataCriteria();
        criteria.createCriteria().andDataCodeEqualTo(dataCode);
        List<BasicData> list = basicDataMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public BasicData queryById(Long id) {
        return basicDataMapper.selectByPrimaryKey(id);
    }

    @Override
    public  long countBasicData(PageRequest pageRequest) {
        BasicDataCriteria basicDataCriteria = new BasicDataCriteria();
        basicDataCriteria.createCriteria();
        return basicDataMapper.countByExample(basicDataCriteria);
    }

    @Override
    public int addBasicData(BasicData basicData) {
      return   basicDataMapper.insertSelective(basicData);

    }

    @Override
    public int updateBasicData(BasicData basicData) {

        return basicDataMapper.updateByPrimaryKey(basicData);
    }
}
