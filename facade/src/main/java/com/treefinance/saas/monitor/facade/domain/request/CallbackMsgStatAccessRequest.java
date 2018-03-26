package com.treefinance.saas.monitor.facade.domain.request;

import java.io.Serializable;
import java.util.Date;

/**
 * Buddha Bless , No Bug !
 *
 * @author haojiahong
 * @date 2018/3/15
 */
public class CallbackMsgStatAccessRequest implements Serializable {
    private static final long serialVersionUID = -9068831447209604225L;

    /**
     * 按任务统计:0
     * 按人数统计:1
     */
    private Byte dataType;

    /**
     * 业务类型
     */
    private Byte bizType;

    /**
     * 商户id
     */
    private String appId;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;


    public Byte getDataType() {
        return dataType;
    }

    public void setDataType(Byte dataType) {
        this.dataType = dataType;
    }

    public Byte getBizType() {
        return bizType;
    }

    public void setBizType(Byte bizType) {
        this.bizType = bizType;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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
