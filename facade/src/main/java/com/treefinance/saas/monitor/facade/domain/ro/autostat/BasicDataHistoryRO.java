package com.treefinance.saas.monitor.facade.domain.ro.autostat;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;

import java.util.Date;

/**
 * Created by yh-treefinance on 2018/5/2.
 */
public class BasicDataHistoryRO extends BaseRO {
    /**
     * 数据历史ID
     */
    private Long id;

    /**
     * 基础数据ID
     */
    private Long basicDataId;

    /**
     * 基础数据编码
     */
    private String basicDataCode;

    /**
     * 数据唯一编码
     */
    private String dataId;

    /**
     * 数据时间
     */
    private Date dataTime;

    /**
     * 数据json
     */
    private String dataJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBasicDataId() {
        return basicDataId;
    }

    public void setBasicDataId(Long basicDataId) {
        this.basicDataId = basicDataId;
    }

    public String getBasicDataCode() {
        return basicDataCode;
    }

    public void setBasicDataCode(String basicDataCode) {
        this.basicDataCode = basicDataCode;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }
}
