package com.treefinance.saas.monitor.facade.domain.result;

import com.treefinance.saas.monitor.facade.domain.base.PageRequest;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 * Created by yh-treefinance on 2017/6/22.
 */
public class PageResult<T> implements Serializable {

    private long timestamp = System.currentTimeMillis();
    private String errorMsg;
    private List<T> data;
    /**
     * 总数
     */
    private long totalCount = 0;
    /**
     * 请求参数
     */
    private PageRequest request;

    public PageResult(PageRequest request) {
        this.request = request;
    }

    public PageResult(PageRequest request, List<T> data, long totalCount) {
        this.data = data;
        this.request = request;
        this.totalCount = totalCount;
    }

    public PageResult(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
