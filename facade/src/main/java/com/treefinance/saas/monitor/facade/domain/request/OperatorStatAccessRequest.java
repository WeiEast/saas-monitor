package com.treefinance.saas.monitor.facade.domain.request;

import com.treefinance.saas.monitor.facade.domain.base.PageRequest;

import java.io.Serializable;
import java.util.Date;

/**
 * 运营商监控查询条件类
 *
 * @author haojiahong
 * @date 2017/11/1
 */
public class OperatorStatAccessRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 4482630849205263759L;

    private Date dataDate;

    private Date startDate;

    private Date endDate;

    private String groupCode;

    public Date getDataDate() {
        return dataDate;
    }

    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
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

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
}
