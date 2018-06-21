package com.treefinance.saas.monitor.common.domain.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class OperatorAccessAlarmMsgDTO extends BaseAlarmMsgDTO {

    private static final long serialVersionUID = -6477590375789936061L;

    private String groupCode;

    private String groupName;

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

}