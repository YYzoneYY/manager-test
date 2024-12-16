package com.ruoyi.common.core.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BaseToLongEntity implements Serializable {

    private static final long serialVersionUID = 10203L;

//    @TableLogic
//    @JSONField(
//            serialize = false
//    )
//    @TableField("is_deleted")
//    private boolean deleted = false;
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    @JsonIgnore
    @TableField(exist = false)
    private String searchValue;

    /** 创建者 */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createBy;


    /** 更新者 */
    @TableField(fill = FieldFill.INSERT)
    private String updateBy;

    /** 更新时间 */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Long updateTime;

    /** 备注 */
    private String remark;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public BaseToLongEntity() {
    }



}
