package com.treefinance.saas.monitor.biz.autostat.basicdata.service;

import com.treefinance.saas.monitor.biz.autostat.base.BaseQueryService;
import com.treefinance.saas.monitor.dao.entity.BasicData;
import com.treefinance.saas.monitor.dao.mapper.BasicDataMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 基础数据服务
 * Created by yh-treefinance on 2018/1/22.
 */
public interface BasicDataService extends BaseQueryService<BasicData> {


    long countBasicData();


   int addBasicData(BasicData basicData);
}
