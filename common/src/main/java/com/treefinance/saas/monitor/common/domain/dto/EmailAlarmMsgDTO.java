package com.treefinance.saas.monitor.common.domain.dto;

/**
 * @author chengtong
 * @date 18/3/12 18:57
 */
public class EmailAlarmMsgDTO extends BaseAlarmMsgDTO {


    /**邮箱*/
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
