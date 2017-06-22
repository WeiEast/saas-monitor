package com.treefinance.saas.monitor.common.domain.dto;

import com.treefinance.saas.monitor.common.domain.BaseDTO;

import java.math.BigDecimal;
import java.util.Date;

public class MerchantStatDayAccessDTO extends BaseDTO {
    /** 数据ID */
    private Long id;

    /** 数据时间 */
    private Date dataTime;

    /** 数据类型：0-合计，1:银行，2：电商，3:邮箱，4:运营商*/
    private Byte dataType;

    /** 商户APPID */
    private String appId;

    /** 访问用户数 */
    private Integer userCount;

    /** 访问总数*/
    private Integer totalCount;

    /** 访问成功数 */
    private Integer successCount;

    /** 访问失败数 */
    private Integer failCount;

    /** 访问取消数 */
    private Integer cancelCount;

    /** 访问成功率 */
    private BigDecimal successRate;

    /** 访问失败率 */
    private BigDecimal failRate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    public Byte getDataType() {
        return dataType;
    }

    public void setDataType(Byte dataType) {
        this.dataType = dataType;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    public Integer getCancelCount() {
        return cancelCount;
    }

    public void setCancelCount(Integer cancelCount) {
        this.cancelCount = cancelCount;
    }

    public BigDecimal getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(BigDecimal successRate) {
        this.successRate = successRate;
    }

    public BigDecimal getFailRate() {
        return failRate;
    }

    public void setFailRate(BigDecimal failRate) {
        this.failRate = failRate;
    }
}