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
    /**记录编号*/
    private Long id;
    /**预警的数据时间*/
    private Date dataTime;
    /**记录状态*/
    private Integer isProcessed;
    /**处理状态描述*/
    private String processDesc;
    /**预警等级*/
    private String level;
    /**预警类型*/
    private String alarmType;
    /**摘要信息*/
    private String summary;
    /**预警内容*/
    private String content;
    /**处理人员名称*/
    private String processorName;
    /**值班人员名字*/
    private String dutyName;
    /**预警工单状态*/
    private Integer orderStatus;
    /**工单状态描述*/
    private String orderStatusDesc;
    /**开始时间*/
    private Date startTime;
    /**结束时间*/
    private Date endTime;
    /**工单编号*/
    private Long orderId;

    /**描述*/
    private String desc;

}
