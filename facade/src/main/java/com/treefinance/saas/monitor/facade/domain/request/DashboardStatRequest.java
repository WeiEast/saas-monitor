package com.treefinance.saas.monitor.facade.domain.request;

import com.treefinance.saas.monitor.facade.domain.base.BaseRequest;
import lombok.Data;

import java.util.Date;

/**
 * @author chengtong
 * @date 18/9/11 14:58
 */
@Data
public class DashboardStatRequest extends BaseRequest {

    private Byte bizType;

    private Date startTime;

    private Date endTime;

    private Byte saasEnv;

}
