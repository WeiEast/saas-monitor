package com.treefinance.saas.monitor.facade.domain.ro;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by haojiahong on 2017/8/15.
 */
public class WebsiteRO implements Serializable {

    private static final long serialVersionUID = 7253858259606364538L;

    private Integer id;

    /**  */
    private String websiteType;

    /** */
    private String websiteName;

    /**
     * 根据websiteName查询到的具体的运营商,银行,电商名称
     */
    private String websiteDetailName;

    /** */
    private String websiteDomain;

    /** */
    private String isEnabled;

    /** */
    private String loginTip;

    /** */
    private String verifyTip;

    /** */
    private Integer initTimeout;

    /** */
    private Integer codeWaitTime;

    /** */
    private Integer loginTimeout;

    /**  */
    private String resetType;

    /** */
    private String smsTemplate;

    /** */
    private String smsReceiver;

    /** */
    private String resetURL;

    /** */
    private String resetTip;

    /** */
    private Date createdAt;

    /** */
    private Date updatedAt;

    /** */
    private Boolean simulate;

    /** */
    private Integer templateId;

    /** */
    private String initSetting;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWebsiteType() {
        return websiteType;
    }

    public void setWebsiteType(String websiteType) {
        this.websiteType = websiteType;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getWebsiteDetailName() {
        return websiteDetailName;
    }

    public void setWebsiteDetailName(String websiteDetailName) {
        this.websiteDetailName = websiteDetailName;
    }

    public String getWebsiteDomain() {
        return websiteDomain;
    }

    public void setWebsiteDomain(String websiteDomain) {
        this.websiteDomain = websiteDomain;
    }

    public String getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getLoginTip() {
        return loginTip;
    }

    public void setLoginTip(String loginTip) {
        this.loginTip = loginTip;
    }

    public String getVerifyTip() {
        return verifyTip;
    }

    public void setVerifyTip(String verifyTip) {
        this.verifyTip = verifyTip;
    }

    public Integer getInitTimeout() {
        return initTimeout;
    }

    public void setInitTimeout(Integer initTimeout) {
        this.initTimeout = initTimeout;
    }

    public Integer getCodeWaitTime() {
        return codeWaitTime;
    }

    public void setCodeWaitTime(Integer codeWaitTime) {
        this.codeWaitTime = codeWaitTime;
    }

    public Integer getLoginTimeout() {
        return loginTimeout;
    }

    public void setLoginTimeout(Integer loginTimeout) {
        this.loginTimeout = loginTimeout;
    }

    public String getResetType() {
        return resetType;
    }

    public void setResetType(String resetType) {
        this.resetType = resetType;
    }

    public String getSmsTemplate() {
        return smsTemplate;
    }

    public void setSmsTemplate(String smsTemplate) {
        this.smsTemplate = smsTemplate;
    }

    public String getSmsReceiver() {
        return smsReceiver;
    }

    public void setSmsReceiver(String smsReceiver) {
        this.smsReceiver = smsReceiver;
    }

    public String getResetURL() {
        return resetURL;
    }

    public void setResetURL(String resetURL) {
        this.resetURL = resetURL;
    }

    public String getResetTip() {
        return resetTip;
    }

    public void setResetTip(String resetTip) {
        this.resetTip = resetTip;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getSimulate() {
        return simulate;
    }

    public void setSimulate(Boolean simulate) {
        this.simulate = simulate;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public String getInitSetting() {
        return initSetting;
    }

    public void setInitSetting(String initSetting) {
        this.initSetting = initSetting;
    }
}
