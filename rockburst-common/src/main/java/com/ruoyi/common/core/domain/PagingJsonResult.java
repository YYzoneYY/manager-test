package com.ruoyi.common.core.domain;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.core.page.Pagination;

import java.util.ArrayList;
import java.util.List;

public class PagingJsonResult<T> extends R<T> {
    private static final long serialVersionUID = 1002L;



    public PagingJsonResult(R<T> jsonResult, Pagination pagination) {
        super(jsonResult.getCode(), jsonResult.getMsg(), (T) pagination);
    }



}
