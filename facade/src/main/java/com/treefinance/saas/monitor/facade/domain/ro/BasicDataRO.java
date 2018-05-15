package com.treefinance.saas.monitor.facade.domain.ro;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;


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
    public String toString() {
        return "BasicDataRO{" +
                "id=" + id +
                ", dataCode='" + dataCode + '\'' +
                ", dataName='" + dataName + '\'' +
                ", dataJson='" + dataJson + '\'' +
                ", dataSource=" + dataSource +
                ", dataSourceConfigJson='" + dataSourceConfigJson +
                '}';
    }
}
