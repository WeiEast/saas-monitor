package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.RealTimeStatAccessDTO;
import com.treefinance.saas.monitor.facade.domain.request.BaseStatAccessRequest;

import java.util.Date;
import java.util.List;

/**
 * Good Luck Bro , No Bug !
 * 实时任务统计,计算前7天平均值
 *
 * @author haojiahong
 * @date 2018/6/27
 */
public interface RealTimeAvgStatAccessService {


    /**
     * 查询实时数据
     *
     * @param request
     * @return
     */
    List<RealTimeStatAccessDTO> queryRealTimeStatAccess(BaseStatAccessRequest request);

    /**
     * 定时存储前平均值数据(7天)
     */
    void saveDataOnFixedTime();

    /**
     * 查询平均值数据
     *
     * @param appId     商户id
     * @param saasEnv   环境
     * @param bizType   业务类型
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param hiddenRecentPoint
     * @return
     */
    List<RealTimeStatAccessDTO> queryDataByConditions(String appId, Byte saasEnv, Byte bizType, Date startTime, Date endTime, Integer intervalMins, Byte hiddenRecentPoint);

}
