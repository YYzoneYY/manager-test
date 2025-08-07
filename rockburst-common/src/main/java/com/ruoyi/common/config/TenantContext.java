package com.ruoyi.common.config;

public class TenantContext {
    private static final ThreadLocal<Long> TENANT = new ThreadLocal<>();

    public static void setMineId(Long mineId) {
        TENANT.set(mineId);
    }

    public static Long getMineId() {
        return TENANT.get();
    }

    public static void clear() {
        TENANT.remove();
    }
}
