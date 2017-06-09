package com.treefinance.saas.monitor.common.domain.vo;

import java.io.Serializable;

/**
 * Created by yh-treefinance on 2017/5/24.
 */
public class LoginUserVO implements Serializable {

    private Integer userId;

    private String userName;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
