package com.treefinance.saas.monitor.facade.domain.request;

/**
 * Created by yh-treefinance on 2017/5/27.
 */
public class MerchantStatMailRequest extends MerchantStatBaseRequest {
    /**
     * 邮箱编码
     */
    private String mailCode;

    public String getMailCode() {
        return mailCode;
    }

    public void setMailCode(String mailCode) {
        this.mailCode = mailCode;
    }
}
