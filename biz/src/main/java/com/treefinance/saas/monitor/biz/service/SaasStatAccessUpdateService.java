package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.SaasStatAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.SaasStatDayAccessDTO;

import java.util.List;

/**
 * 统计数据更新Service
 * Created by yh-treefinance on 2017/6/1.
 */
public interface SaasStatAccessUpdateService {

    /**
     * 批量保存商户访问数据
     *
     * @param list
     */
    void batchInsertStatAccess(List<SaasStatAccessDTO> list);

    /**
     * 批量保存商户日访问数据
     *
     * @param list
     */
    void batchInsertStaDayAccess(List<SaasStatDayAccessDTO> list);

}
