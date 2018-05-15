package com.treefinance.saas.monitor.facade.domain.request.autostat;

import com.treefinance.saas.monitor.facade.domain.base.PageRequest;

import java.util.Date;

/**
 * Created by yh-treefinance on 2018/5/2.
 */
public class BasicDataHistoryRequest extends PageRequest {

    /**
     * 基础数据ID
     */
    private Long basicDataId;
    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    public Long getBasicDataId() {
        return basicDataId;
    }

    public void setBasicDataId(Long basicDataId) {
        this.basicDataId = basicDataId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
