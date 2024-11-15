package com.ruoyi.common.core.page;


import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public class MPage<T> {
    private long total;
    private List<T> rows;

    // 构造函数
    public MPage(IPage<T> page) {
        this.total = page.getTotal();
        this.rows = page.getRecords();
    }

    // Getter 和 Setter 方法
    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
