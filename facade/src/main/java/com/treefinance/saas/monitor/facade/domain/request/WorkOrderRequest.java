package com.treefinance.saas.monitor.facade.domain.request;

import com.treefinance.saas.monitor.facade.domain.base.PageRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author chengtong
 * @date 18/5/30 11:27
 */

@Setter
@Getter
public class WorkOrderRequest extends PageRequest {
    /**
     * 值班人员 模糊查询
     * */
    private String dutyName;

    /**
     * 处理人员 模糊查询
     * */
    private String processorName;

    /**
     * 对创建时间的范围查询 开始
     * */
    private Date startTime;

    /**
     * 对创建时间的范围查询 结束
     * */
    private Date endTime;

    /**
     * 编号
     * */
    private Long id;

}
