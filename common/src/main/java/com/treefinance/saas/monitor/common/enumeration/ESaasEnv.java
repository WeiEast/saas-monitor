package com.treefinance.saas.monitor.common.enumeration;

/**
 * @author chengtong
 * @date 18/5/11 16:17
 */
public enum  ESaasEnv {

    ALL(0,"所有环境"),
    PRODUCT(1,"生产环境"),
    PRE_PRODUCT(2,"预发布环境")
    ;

    private int value;
    private String desc;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    ESaasEnv(int code, String desc){
        this.value = code;
        this.desc =desc;
    }

    public ESaasEnv getByValue(int value){
        for(ESaasEnv saasEnv:values()){
            if(saasEnv.value == value){
                return saasEnv;
            }
        }
        return null;
    }



}
