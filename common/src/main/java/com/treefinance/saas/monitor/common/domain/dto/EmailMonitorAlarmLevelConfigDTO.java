package com.treefinance.saas.monitor.common.domain.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author chengtong
 * @date 18/3/13 14:20
 */
public class EmailMonitorAlarmLevelConfigDTO implements Serializable{

    private String level;

    private List<String> channels;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }
}
