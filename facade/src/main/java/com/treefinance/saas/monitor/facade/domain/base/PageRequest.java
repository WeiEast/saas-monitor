package com.treefinance.saas.monitor.facade.domain.base;

/**
 * Created by yh-treefinance on 2017/6/22.
 */
public class PageRequest extends BaseRequest {
    /**
     * 当前页
     */
    private int pageNumber = 1;
    /**
     * 每页显示数
     */
    private int pageSize = 20;
    /**
     * 偏移量
     */
    private int offset = 0;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getOffset() {
        offset = (pageNumber - 1) * pageSize;
        return offset;
    }
}
