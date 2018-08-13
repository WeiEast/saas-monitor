package com.treefinance.saas.monitor.facade.domain.ro;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;
import lombok.Data;

/**
 * @author chengtong
 * @date 18/8/13 11:01
 */
@Data
public class AlarmRecordStatisticRO extends BaseRO {

    /**
     * 预警名称
     * */
    private String name;

    /**
     * 次数
     * */
    private Integer count;

    /**
     * 时长
     * */
    private Double duration;

    /**
     * 平均时长
     * */
    private Double durationAver;

    /**
     * 单次最大时长
     * */
    private Double maxDuration;

    /**
     * 已处理次数
     * */
    private Integer processedCount;

    /**
     * 误报次数
     * */
    private Integer wrongCount;

    /**
     * 无法解决次数
     * */
    private Integer disableCount;

    /**
     * 系统恢复次数
     * */
    private Integer recoveryCount;


}
