package com.treefinance.saas.monitor.biz.autostat.basicdata.service;

import com.treefinance.saas.monitor.biz.autostat.base.BaseQueryService;
import com.treefinance.saas.monitor.dao.entity.BasicData;
import com.treefinance.saas.monitor.facade.domain.base.PageRequest;

import java.util.List;

/**
 * 基础数据服务
 * Created by yh-treefinance on 2018/1/22.
 */
public interface BasicDataService extends BaseQueryService<BasicData> {


    long countBasicData(PageRequest pageRequest);


    int addBasicData(BasicData basicData);


    List<BasicData> queryAllBasicDataWithPaging(PageRequest pageRequest);

    int updateBasicData(BasicData basicData);
}
