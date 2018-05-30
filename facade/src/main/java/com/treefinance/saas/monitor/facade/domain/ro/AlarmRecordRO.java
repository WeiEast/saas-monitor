package com.treefinance.saas.monitor.facade.domain.ro;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author chengtong
 * @date 18/5/30 11:17
 */
@Setter
@Getter
public class AlarmRecordRO extends BaseRO {

    private Long id;

    private Date dataTime;

    private Boolean isProcessed;

    private String level;

    private String alarmType;

    private String summary;

    private String content;

}
