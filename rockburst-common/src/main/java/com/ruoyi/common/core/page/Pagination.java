package com.ruoyi.common.core.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

public class Pagination extends Page {

    private static final Logger log = LoggerFactory.getLogger(Pagination.class);
    private static final long serialVersionUID = -4083929594112114522L;
    private int pageNum = 1;
    private int pageSize = 20;
    private long total = 0L;
    private static final String DEFAULT_ORDER_BY;
    private String orderBy;

    private Object rows;

    public Object getRows() {
        return rows;
    }

    public void setRows(Object rows) {
        this.rows = rows;
    }

    public int getPageIndex() {
        return this.pageNum;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public long getTotalCount() {
        return this.total;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public Pagination setPageIndex(final int pageIndex) {
        this.pageNum = pageIndex;
        return this;
    }

    public Pagination setTotalCount(final long total) {
        this.total = total;
        return this;
    }

    public Pagination setOrderBy(final String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    static {
        DEFAULT_ORDER_BY = FieldName.id.name() + ":" + "DESC";
    }


    public static enum FieldName {
        id,
        tenantId,
        parentId,
        children,
        deleted,
        createTime,
        updateTime,
        createBy,
        updateBy,
        orgId,
        userId;

        private FieldName() {
        }
    }
}
