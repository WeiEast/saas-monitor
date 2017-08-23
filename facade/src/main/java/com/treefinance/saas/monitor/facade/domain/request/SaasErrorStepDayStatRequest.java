package com.treefinance.saas.monitor.facade.domain.request;

import com.treefinance.saas.monitor.facade.domain.base.PageRequest;

import java.util.Date;

/**
 * Created by haojiahong on 2017/8/22.
 */
public class SaasErrorStepDayStatRequest extends PageRequest {

    private static final long serialVersionUID = 7382943546253267663L;
    /**
     * 数据类型：0:合计，1:银行，2：电商，3:邮箱，4:运营商
     */
    private Byte dataType;

    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 结束时间
     */
    private Date endDate;

    public Byte getDataType() {
        return dataType;
    }

    public void setDataType(Byte dataType) {
        this.dataType = dataType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
