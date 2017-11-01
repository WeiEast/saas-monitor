package com.treefinance.saas.monitor.common.enumeration;

/**
 * Created by haojiahong on 2017/10/27.
 */
public enum ETaskOperatorStatus {

    COMFIRM_MOBILE((byte) 0, "确认手机号"),
    LOGIN((byte) 1, "开始登陆"),
    CRAWL_FAIL((byte) 2, "抓取失败"),
    PROCESS_FAIL((byte) 3, "洗数失败");

    private Byte status;
    private String name;

    ETaskOperatorStatus(Byte status, String name) {
        this.status = status;
        this.name = name;
    }

    public Byte getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public static ETaskOperatorStatus getStatus(Byte code) {
        if (code != null) {
            for (ETaskOperatorStatus item : ETaskOperatorStatus.values()) {
                if (item.status.equals(code)) {
                    return item;
                }
            }
        }
        return null;
    }
}
