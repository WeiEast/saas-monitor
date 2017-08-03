package com.treefinance.saas.monitor.facade.domain.ro.stat.api;

/**
 * Created by haojiahong on 2017/7/7.
 */
public class ApiStatDayAccessRO extends ApiBaseStatRO {

    private String appId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
