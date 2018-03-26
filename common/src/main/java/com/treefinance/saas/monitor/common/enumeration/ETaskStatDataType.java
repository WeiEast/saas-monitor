package com.treefinance.saas.monitor.common.enumeration;

/**
 * Created by luoyihua on 2017/4/27.
 */
public enum ETaskStatDataType {
    TASK((byte) 0, "按任务数统计"),
    USER((byte) 1, "按人数统计");

    private Byte code;
    private String text;

    private ETaskStatDataType(Byte code, String text) {
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

    public static ETaskStatDataType getByValue(int code){
        for(ETaskStatDataType type :values()){
            if(type.code == code){
                return type;
            }
        }
        return null;
    }

}
