package com.treefinance.saas.monitor.facade.domain.request;

/**
 * Created by yh-treefinance on 2017/5/27.
 */
public class MerchantStatBankRequest extends MerchantStatBaseRequest {
    /**
     * 银行ID
     */
    private Short bankId;

    public Short getBankId() {
        return bankId;
    }

    public void setBankId(Short bankId) {
        this.bankId = bankId;
    }
}
