package com.treefinance.saas.monitor.facade.domain.ro.stat;

public class MerchantStatDayAccessRO extends BaseStatRO {

    /** 数据类型：0-合计，1:银行，2：电商，3:邮箱，4:运营商*/
    private Byte dataType;

    public Byte getDataType() {
        return dataType;
    }

    public void setDataType(Byte dataType) {
        this.dataType = dataType;
    }
}