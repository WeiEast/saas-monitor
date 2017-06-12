package com.treefinance.saas.monitor.facade.domain.ro;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;

/**
 * Created by yh-treefinance on 2017/6/12.
 */
public class MailRO extends BaseRO {
    /**
     * 邮箱编码
     */
    private String mailCode;
    /**
     * 邮箱名称
     */
    private String mailName;

    public String getMailCode() {
        return mailCode;
    }

    public void setMailCode(String mailCode) {
        this.mailCode = mailCode;
    }

    public String getMailName() {
        return mailName;
    }

    public void setMailName(String mailName) {
        this.mailName = mailName;
    }
}
