package com.ruoyi.system.domain.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("分页数据返回值")
public class PageSimple<T> {

    @ApiModelProperty("当前页")
    private long pageNumber;
    @ApiModelProperty("每页数据量")
    private long pageSize;
    @ApiModelProperty("总记录数")
    private long total;
    @ApiModelProperty("数据")
    private List<T> rows;

    public static <T> PageSimple<T> build(IPage<T> page) {
        PageSimple<T> pageSimple = new PageSimple<>();
        pageSimple.setPageNumber(page.getCurrent());
        pageSimple.setPageSize(page.getSize());
        pageSimple.setTotal(page.getTotal());
        pageSimple.setRows(page.getRecords());
        return pageSimple;
    }



}
