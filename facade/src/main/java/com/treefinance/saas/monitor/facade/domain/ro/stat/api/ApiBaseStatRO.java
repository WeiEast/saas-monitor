package com.treefinance.saas.monitor.facade.domain.ro.stat.api;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;

import java.util.Date;

/**
 * Created by haojiahong on 2017/7/7.
 */
public class ApiBaseStatRO extends BaseRO {

    /**
     * 数据ID
     */
    private Long id;

    /**
     * 数据时间
     */
    private Date dataTime;

    /**
     * 访问总数
     */
    private Integer totalCount;

    /**
     * http状态码为2xx的数量
     */
    private Integer http2xxCount;

    /**
     * http状态码为4xx的数量
     */
    private Integer http4xxCount;

    /**
     * http状态码为5xx的数量
     */
    private Integer http5xxCount;

    /**
     * 平均响应时间，毫秒
     */
    private Integer avgResponseTime;

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

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getHttp2xxCount() {
        return http2xxCount;
    }

    public void setHttp2xxCount(Integer http2xxCount) {
        this.http2xxCount = http2xxCount;
    }

    public Integer getHttp4xxCount() {
        return http4xxCount;
    }

    public void setHttp4xxCount(Integer http4xxCount) {
        this.http4xxCount = http4xxCount;
    }

    public Integer getHttp5xxCount() {
        return http5xxCount;
    }

    public void setHttp5xxCount(Integer http5xxCount) {
        this.http5xxCount = http5xxCount;
    }

    public Integer getAvgResponseTime() {
        return avgResponseTime;
    }

    public void setAvgResponseTime(Integer avgResponseTime) {
        this.avgResponseTime = avgResponseTime;
    }
}
