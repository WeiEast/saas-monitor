package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.OperatorDTO;
import com.treefinance.saas.monitor.dao.entity.Operator;

import java.util.List;

/**
 * 运营商服务
 * Created by yh-treefinance on 2017/6/8.
 */
public interface OperatorService {
    /**
     * 获取所有运营商信息
     *
     * @return
     */
    List<OperatorDTO> getAll();

    /**
     * 根据websiteId查询运营商信息
     *
     * @param websiteId
     * @return
     */
    OperatorDTO getOperatorByWebsiteId(Integer websiteId);

    /**
     * 根据website名称查询运营商信息
     *
     * @param website
     * @return
     */
    OperatorDTO getOperatorByWebsite(String website);
}
