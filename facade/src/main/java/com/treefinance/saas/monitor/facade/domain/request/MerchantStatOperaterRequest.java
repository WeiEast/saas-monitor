package com.treefinance.saas.monitor.facade.domain.request;

/**
 * Created by yh-treefinance on 2017/5/27.
 */
public class MerchantStatOperaterRequest extends MerchantStatBaseRequest {
    /**
     * 运营商类型: 1-中国移动，2-中国联通，3-中国电信
     */
    private Integer operaterType;

    /**
     * 运营商ID
     */
    private String operaterId;

    public Integer getOperaterType() {
        return operaterType;
    }

    public void setOperaterType(Integer operaterType) {
        this.operaterType = operaterType;
    }

    public String getOperaterId() {
        return operaterId;
    }

    public void setOperaterId(String operaterId) {
        this.operaterId = operaterId;
    }
}
