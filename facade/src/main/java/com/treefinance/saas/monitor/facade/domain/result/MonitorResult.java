/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.monitor.facade.domain.result;

import com.treefinance.saas.monitor.facade.domain.base.PageRequest;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;

/**
 * 监控工具类
 *
 * @param <T>
 */
public class MonitorResult<T> implements Serializable {
    private long timestamp = System.currentTimeMillis();
    private String errorMsg;
    private T data;
    /**
     * 总数
     */
    private long totalCount = 0;
    /**
     * 请求参数
     */
    private PageRequest request;

    public MonitorResult() {
    }

    public MonitorResult(T data) {
        this.data = data;
    }

    public MonitorResult(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public MonitorResult(long timestamp, String errorMsg, T data) {
            this.timestamp = timestamp;
            this.errorMsg = errorMsg;
            this.data = data;
    }

    public MonitorResult(PageRequest request, T data, long totalCount) {
        this.data = data;
        this.request = request;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public PageRequest getRequest() {
        return request;
    }

    public void setRequest(PageRequest request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
