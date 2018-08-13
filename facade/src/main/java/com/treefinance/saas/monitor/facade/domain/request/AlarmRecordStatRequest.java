package com.treefinance.saas.monitor.facade.domain.request;

import com.treefinance.saas.monitor.facade.domain.base.BaseRequest;
import lombok.Data;

import java.util.Date;

/**
 * @author chengtong
 * @date 18/8/13 11:34
 */
@Data
public class AlarmRecordStatRequest extends BaseRequest {

    private String name;

    private Date startTime;

    private Date endTime;

}
