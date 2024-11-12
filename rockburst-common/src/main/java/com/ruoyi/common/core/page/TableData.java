package com.ruoyi.common.core.page;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/12
 * @description: 表格分页数据对象
 */

@Data
public class TableData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<?> rows;

    /**
     * 表格数据对象
     */
    public TableData(){
    }

    public TableDataInfo getDataTable(List<?> list){
        TableDataInfo rspData = new TableDataInfo();
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 分页
     *
     * @param list 列表数据
     * @param total 总记录数
     */
    public TableData(List<?> list, int total){
        this.rows = list;
        this.total = total;
    }
}