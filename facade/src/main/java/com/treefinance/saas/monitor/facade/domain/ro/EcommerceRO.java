package com.treefinance.saas.monitor.facade.domain.ro;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;

import java.util.Date;

/**
 * Created by yh-treefinance on 2017/6/8.
 */
public class EcommerceRO extends BaseRO {
    /** */
    private Short id;

    /** */
    private Integer websiteId;

    /**  */
    private String ecommerceName;

    /** */
    private String isEnabled;

    /** */
    private Date createdAt;

    /** */
    private Date updatedAt;

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public Integer getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(Integer websiteId) {
        this.websiteId = websiteId;
    }

    public String getEcommerceName() {
        return ecommerceName;
    }

    public void setEcommerceName(String ecommerceName) {
        this.ecommerceName = ecommerceName;
    }

    public String getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled;
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
}
