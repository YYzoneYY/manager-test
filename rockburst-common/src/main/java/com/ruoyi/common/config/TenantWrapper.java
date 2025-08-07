package com.ruoyi.common.config;

import com.github.yulichang.wrapper.MPJLambdaWrapper;

public class TenantWrapper<T> extends MPJLambdaWrapper<T> {
    public TenantWrapper() {
        Long mineId = TenantContext.getMineId();
        if (mineId != null) {
            this.eq("t.mine_id", mineId);
        }
    }
}
