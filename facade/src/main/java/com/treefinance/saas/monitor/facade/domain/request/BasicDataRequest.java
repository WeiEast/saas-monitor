package com.treefinance.saas.monitor.facade.domain.request;

import com.treefinance.saas.monitor.facade.domain.base.BaseRequest;

/**
 * @author:guoguoyun
 * @date:Created in 2018/4/23下午7:38
 */
public class BasicDataRequest extends BaseRequest {

    private Long   id;


    private String dataCode;


    private String dataName;


    private String dataJson;


    private Byte dataSource;


    private String dataSourceConfigJson;

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

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BasicDataRequest{" +
                "id=" + id +
                ", dataCode='" + dataCode + '\'' +
                ", dataName='" + dataName + '\'' +
                ", dataJson='" + dataJson + '\'' +
                ", dataSource=" + dataSource +
                ", dataSourceConfigJson='" + dataSourceConfigJson + '\'' +
                '}';
    }
}
