package com.treefinance.saas.monitor.common.domain.dto;

/**
 * @author chengtong
 * @date 18/3/12 16:36
 */
public class EmailStatAccessDTO extends BaseStatAccessDTO{

    private static final long serialVersionUID = -5390308997672642434L;

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
