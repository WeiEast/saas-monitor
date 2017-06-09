package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.EcommerceDTO;
import com.treefinance.saas.monitor.dao.entity.Ecommerce;

/**
 * Created by yh-treefinance on 2017/6/8.
 */
public interface EcommerceService {

    /**
     * 根据website
     * @param website
     * @return
     */
    EcommerceDTO getEcommerceByWebsite(String website);
}
