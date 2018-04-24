package com.treefinance.saas.monitor.facade.domain.ro;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;

import java.util.Date;

/**
 * @author:guoguoyun
 * @date:Created in 2018/4/23下午5:25
 */
public class BasicDataRO extends BaseRO {

    private Long id;


    private String dataCode;


    private String dataName;


    private String dataJson;


    private Byte dataSource;


    private String dataSourceConfigJson;


    private Date createTime;


    private Date lastUpdateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataCode() {
        return dataCode;
    }

    public void setDataCode(String dataCode) {
        this.dataCode = dataCode;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public Byte getDataSource() {
        return dataSource;
    }

    public void setDataSource(Byte dataSource) {
        this.dataSource = dataSource;
    }

    public String getDataSourceConfigJson() {
        return dataSourceConfigJson;
    }

    public void setDataSourceConfigJson(String dataSourceConfigJson) {
        this.dataSourceConfigJson = dataSourceConfigJson;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Override
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "BasicDataRO{" +
                "id=" + id +
                ", dataCode='" + dataCode + '\'' +
                ", dataName='" + dataName + '\'' +
                ", dataJson='" + dataJson + '\'' +
                ", dataSource=" + dataSource +
                ", dataSourceConfigJson='" + dataSourceConfigJson + '\'' +
                ", createTime=" + createTime +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}
