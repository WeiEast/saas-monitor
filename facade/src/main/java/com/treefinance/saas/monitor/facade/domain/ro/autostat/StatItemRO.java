package com.treefinance.saas.monitor.facade.domain.ro.autostat;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;

import java.util.Date;

public class StatItemRO extends BaseRO {
    /**
     * id
     */
    private Long id;

    /**
     * 数据项编码
     */
    private String itemCode;

    /**
     * 数据项名称
     */
    private String itemName;

    /**
     * 数据计算表达式
     */
    private String itemExpression;

    /**
     * 是否存储
     */
    private Byte isStore;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 数据来源
     */
    private Byte dataSource;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemExpression() {
        return itemExpression;
    }

    public void setItemExpression(String itemExpression) {
        this.itemExpression = itemExpression;
    }

    public Byte getIsStore() {
        return isStore;
    }

    public void setIsStore(Byte isStore) {
        this.isStore = isStore;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Byte getDataSource() {
        return dataSource;
    }

    public void setDataSource(Byte dataSource) {
        this.dataSource = dataSource;
    }
}