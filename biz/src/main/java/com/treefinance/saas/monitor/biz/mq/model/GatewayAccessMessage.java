package com.treefinance.saas.monitor.biz.mq.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 网关访问消息
 * Created by yh-treefinance on 2017/6/6.
 */
public class GatewayAccessMessage implements Serializable {
    /**
     * 任务ID
     */
    private Long taskId;
    /**
     * 用户ID
     */
    private String uniqueId;
    /**
     * 商户ID
     */
    private String appId;
    /**
     * 账户名称
     */
    private String accountNo;

    /**
     * 站点名称
     */
    private String webSite;
    /**
     * 业务类型
     */
    private Byte bizType;
    /**
     * 任务状态
     */
    private Byte status;
    /**
     * 任务完成时间
     */
    private Date completeTime;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public Byte getBizType() {
        return bizType;
    }

    public void setBizType(Byte bizType) {
        this.bizType = bizType;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}
