package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.MerchantAccessHistoryDTO;

/**
 * 访问历史
 * Created by yh-treefinance on 2017/6/22.
 */
public interface AccessHistoryService {

    /**
     * 插入历史数据
     * @param dto
     */
    void insertAccessHistory(MerchantAccessHistoryDTO dto);
}
