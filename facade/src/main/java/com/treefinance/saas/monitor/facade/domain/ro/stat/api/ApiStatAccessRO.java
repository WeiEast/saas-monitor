package com.treefinance.saas.monitor.facade.domain.ro.stat.api;

/**
 * Created by haojiahong on 2017/7/7.
 */
public class ApiStatAccessRO extends ApiBaseStatRO {

    private String apiUrl;

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
