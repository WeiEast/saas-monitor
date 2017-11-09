package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.OperatorAllStatDayAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorStatAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorStatDayAccessDTO;

import java.util.List;

/**
 * Created by haojiahong on 2017/11/3.
 */
public interface OperatorStatAccessUpdateService {

    /**
     * 批量保存所有运营商日统计数据
     *
     * @param list
     */
    void batchInsertAllOperatorStatDayAccess(List<OperatorAllStatDayAccessDTO> list);

    /**
     * 批量保存运营商日统计数据
     *
     * @param list
     */
    void batchInsertOperatorStatDayAccess(List<OperatorStatDayAccessDTO> list);

    /**
     * 批量保存运营商统计数据
     *
     * @param list
     */
    void batchInsertOperatorStatAccess(List<OperatorStatAccessDTO> list);


}
