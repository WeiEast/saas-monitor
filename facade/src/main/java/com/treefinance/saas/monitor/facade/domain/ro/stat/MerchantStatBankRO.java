package com.treefinance.saas.monitor.facade.domain.ro.stat;

public class MerchantStatBankRO extends BaseStatRO {

    /** 银行ID */
    private Short bankId;

    public Short getBankId() {
        return bankId;
    }

    public void setBankId(Short bankId) {
        this.bankId = bankId;
    }
}