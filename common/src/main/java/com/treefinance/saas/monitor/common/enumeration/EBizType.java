package com.treefinance.saas.monitor.common.enumeration;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by luoyihua on 2017/4/27.
 */
public enum EBizType {
    EMAIL("EMAIL", (byte) 1),
    ECOMMERCE("ECOMMERCE", (byte) 2),
    OPERATOR("OPERATOR", (byte) 3),
    FUND("FUND", (byte) 4);

    private Byte code;
    private String text;

    private EBizType(String text, Byte code) {
        this.code = code;
        this.text = text;
    }

    public Byte getCode() {
        return code;
    }

    public void setCode(Byte code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static Byte getCode(String text) {
        if (StringUtils.isNotEmpty(text)) {
            for (EBizType item : EBizType.values()) {
                if (text.equals(item.getText())) {
                    return item.getCode();
                }
            }
        }
        return -1;
    }


    public static EBizType getBizType(Byte code) {
        if (code != null) {
            for (EBizType item : EBizType.values()) {
                if (item.code.equals(code)) {
                    return item;
                }
            }
        }
        return null;
    }
}
