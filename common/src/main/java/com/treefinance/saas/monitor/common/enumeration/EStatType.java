package com.treefinance.saas.monitor.common.enumeration;

/**
 * Created by yh-treefinance on 2017/6/7.
 */
public enum EStatType {
    // 数据类型：0-合计，1:银行，2：电商，3:邮箱，4:运营商,5:公积金
    TOTAL((byte) 0, "TOTAL", "合计"),
    BANK((byte) 1, "BANK", "银行"),
    ECOMMERCE((byte) 2, "ECOMMERCE", "电商"),
    EMAIL((byte) 3, "EMAIL", "邮箱"),
    OPERATOR((byte) 4, "OPERATOR", "运营商"),
    FUND((byte) 5, "FUND", "公积金");

    private Byte type;
    private String text;
    private String name;

    EStatType(Byte type, String text, String name) {
        this.type = type;
        this.text = text;
        this.name = name;
    }

    public Byte getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public static EStatType getById(Byte type) {
        if (type == null) {
            return null;
        }
        for (EStatType statType : EStatType.values()) {
            if (type.equals(statType.getType())) {
                return statType;
            }
        }
        return null;
    }
}
