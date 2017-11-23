package com.treefinance.saas.monitor.common.enumeration;

/**
 * Created by yh-treefinance on 2017/6/7.
 */
public enum ETaskStatus {
    RUNNING((byte) 0, "进行中"), CANCEL((byte) 1, "取消"), SUCCESS((byte) 2, "成功"), FAIL((byte) 3, "失败");

    private Byte status;
    private String name;

    ETaskStatus(Byte status, String name) {
        this.status = status;
        this.name = name;
    }

    public Byte getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public static ETaskStatus getTaskStatusByStatus(Byte status) {
        for (ETaskStatus taskStatus : ETaskStatus.values()) {
            if (taskStatus.getStatus().equals(status)) {
                return taskStatus;
            }
        }
        return null;
    }
}
