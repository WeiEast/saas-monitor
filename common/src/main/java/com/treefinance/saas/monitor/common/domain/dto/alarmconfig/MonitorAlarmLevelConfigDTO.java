package com.treefinance.saas.monitor.common.domain.dto.alarmconfig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author chengtong
 * @date 18/3/13 14:20
 */
@Setter
@Getter
@ToString
public class MonitorAlarmLevelConfigDTO implements Serializable{

    private String level;

    private List<String> channels;

}
