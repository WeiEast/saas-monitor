package com.treefinance.saas.monitor.common.enumeration;

/**
 * Created by yh-treefinance on 2017/6/7.
 */
public enum EStatType {
    // 数据类型：0-合计，1:银行，2：电商，3:邮箱，4:运营商
    TOTAL((byte) 0), BANK((byte) 1), ECOMMERCE((byte) 2), EMAIL((byte) 3), OPERATER((byte) 4);

    private Byte type;

    EStatType(Byte type) {
        this.type = type;
    }

    public Byte getType() {
        return type;
    }
}
