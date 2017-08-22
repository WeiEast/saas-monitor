package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.SaasErrorDayStatDTO;

import java.util.List;

/**
 * 任务失败取消环节统计数据更新Service
 * Created by yh-treefinance on 2017/6/1.
 */
public interface SaasErrorDayStatUpdateService {
    /**
     * 批量保存商户日访问数据
     *
     * @param list
     */
    void batchInsertErrorDayStat(List<SaasErrorDayStatDTO> list);

}
