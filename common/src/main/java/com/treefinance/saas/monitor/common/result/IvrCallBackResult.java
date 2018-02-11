package com.treefinance.saas.monitor.common.result;

/**
 * @author:guoguoyun
 * @date:Created in 2018/1/31下午3:44
 */
public class IvrCallBackResult {

    /**
     * 系统
     */
    private String system;
    /**
     * 用户分组
     */
    private Byte useGroup;

    /**
     * 模块ID
     */
    private Integer modelId;

    /**
     * 业务关联ID
     */
    private Long refId;
    /**
     * 语音电话任务ID
     */
    private Long taskId;
    /**
     * 状态 success-成功,cancel-取消,fail-失败
     */
    private String status;

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public Byte getUseGroup() {
        return useGroup;
    }

    public void setUseGroup(Byte useGroup) {
        this.useGroup = useGroup;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ivrTest{" +
                "system='" + system + '\'' +
                ", useGroup=" + useGroup +
                ", modelId=" + modelId +
                ", refId=" + refId +
                ", taskId=" + taskId +
                ", status='" + status + '\'' +
                '}';
    }
}
