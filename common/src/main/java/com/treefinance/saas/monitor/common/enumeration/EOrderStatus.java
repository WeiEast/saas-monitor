package com.treefinance.saas.monitor.common.enumeration;

/**
 * @author chengtong
 * @date 18/5/28 17:59
 */
public enum EOrderStatus {

    UNPROCESS(0, "未处理"),
    PROCESSED(1, "已处理"),
    WRONG(2, "误报"),;

    private Integer code;
    private String desc;

    EOrderStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EOrderStatus getByValue(Integer code) {
        for (EOrderStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
