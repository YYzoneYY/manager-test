package com.ruoyi.common.enums;

/**
 * 业务操作类型
 *开采状态：0 未开采   1 已开采  2 开采完成
 * @author JXw
 */
public enum WorkFaceType
{
    wkc(0, "未开采"),
    ykc(1, "开采中"),
    kcw(2, "开采完成"),
    tzkc(3, "停止开采"),
    aqfb(4, "安全封闭");

    private final Integer code;
    private final String info;


    WorkFaceType(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public Integer getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }
}