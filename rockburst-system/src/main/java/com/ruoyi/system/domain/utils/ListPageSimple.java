package com.ruoyi.system.domain.utils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * list分页
 */
@Component
public class ListPageSimple {
    public Page getPages(Integer currentPage, Integer pageSize, List list) {

        Page page = new Page();
        if (list == null) {
            return null;
        }
        int size = list.size();
        if (pageSize > size) {
            pageSize = size;
        }
        if (pageSize != 0) {
            int MaxPage = size % pageSize == 0 ? size / pageSize : size / pageSize + 1;
            if (currentPage > MaxPage) {
                currentPage = MaxPage;
            }
        }
        int curldx = currentPage > 1 ? (currentPage - 1) * pageSize : 0;
        ArrayList pageList = new ArrayList();
        for (int i = 0; i < pageSize && curldx + i < size; i++) {
            pageList.add(list.get(curldx + i));
        }
        page.setCurrent(currentPage).setSize(pageSize).setTotal(list.size()).setRecords(pageList);
        return page;
    }
}
