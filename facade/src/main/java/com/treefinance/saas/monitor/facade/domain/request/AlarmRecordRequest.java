package com.treefinance.saas.monitor.facade.domain.request;

import com.treefinance.saas.monitor.facade.domain.base.BaseRequest;
import com.treefinance.saas.monitor.facade.domain.base.PageRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author chengtong
 * @date 18/5/30 11:25
 */
@Setter
@Getter
public class AlarmRecordRequest extends PageRequest {

    /**数据表中的dataTime的范围查询 开始时间*/
    private Date startTime;
    /**数据表中的dataTime的范围查询 结束时间*/
    private Date endTime;
    /**预警类型*/
    private String alarmType;
    /**摘要信息 模糊查询*/
    private String summary;

}
