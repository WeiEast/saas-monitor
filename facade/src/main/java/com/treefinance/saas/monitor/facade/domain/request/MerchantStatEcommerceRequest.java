package com.treefinance.saas.monitor.facade.domain.request;

/**
 * Created by yh-treefinance on 2017/5/27.
 */
public class MerchantStatEcommerceRequest extends MerchantStatBaseRequest {
    /**
     * 电商ID
     */
    private Short ecommerceId;

    public Short getEcommerceId() {
        return ecommerceId;
    }

    public void setEcommerceId(Short ecommerceId) {
        this.ecommerceId = ecommerceId;
    }
}
