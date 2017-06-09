package com.treefinance.saas.monitor.dao.entity;

import java.io.Serializable;
import java.util.Date;

public class Ecommerce implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_ecommerce.Id
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    private Short id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_ecommerce.WebsiteId
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    private Integer websiteId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_ecommerce.EcommerceName
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    private String ecommerceName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_ecommerce.IsEnabled
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    private String isEnabled;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_ecommerce.CreatedAt
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    private Date createdAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_ecommerce.UpdatedAt
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    private Date updatedAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_ecommerce
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_ecommerce.Id
     *
     * @return the value of t_ecommerce.Id
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    public Short getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_ecommerce.Id
     *
     * @param id the value for t_ecommerce.Id
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    public void setId(Short id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_ecommerce.WebsiteId
     *
     * @return the value of t_ecommerce.WebsiteId
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    public Integer getWebsiteId() {
        return websiteId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_ecommerce.WebsiteId
     *
     * @param websiteId the value for t_ecommerce.WebsiteId
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    public void setWebsiteId(Integer websiteId) {
        this.websiteId = websiteId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_ecommerce.EcommerceName
     *
     * @return the value of t_ecommerce.EcommerceName
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    public String getEcommerceName() {
        return ecommerceName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_ecommerce.EcommerceName
     *
     * @param ecommerceName the value for t_ecommerce.EcommerceName
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    public void setEcommerceName(String ecommerceName) {
        this.ecommerceName = ecommerceName == null ? null : ecommerceName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_ecommerce.IsEnabled
     *
     * @return the value of t_ecommerce.IsEnabled
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    public String getIsEnabled() {
        return isEnabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_ecommerce.IsEnabled
     *
     * @param isEnabled the value for t_ecommerce.IsEnabled
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled == null ? null : isEnabled.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_ecommerce.CreatedAt
     *
     * @return the value of t_ecommerce.CreatedAt
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_ecommerce.CreatedAt
     *
     * @param createdAt the value for t_ecommerce.CreatedAt
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_ecommerce.UpdatedAt
     *
     * @return the value of t_ecommerce.UpdatedAt
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_ecommerce.UpdatedAt
     *
     * @param updatedAt the value for t_ecommerce.UpdatedAt
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_ecommerce
     *
     * @mbggenerated Thu Jun 08 13:58:11 CST 2017
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", websiteId=").append(websiteId);
        sb.append(", ecommerceName=").append(ecommerceName);
        sb.append(", isEnabled=").append(isEnabled);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}