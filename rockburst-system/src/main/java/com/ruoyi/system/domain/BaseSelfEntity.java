package com.ruoyi.system.domain;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class BaseSelfEntity implements Serializable {

    private static final long serialVersionUID = 10203L;

//    @TableLogic
//    @JSONField(
//            serialize = false
//    )
//    @TableField("is_deleted")
//    private boolean deleted = false;
    @JSONField(
            format = "yyyy-MM-dd HH:mm:ss"
    )
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @JsonIgnore
    @TableField(exist = false)
    private String searchValue;

    /** 创建者 */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;


    /** 更新者 */
    @TableField(fill = FieldFill.INSERT)
    private String updateBy;

    /** 更新时间 */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 备注 */
    private String remark;

    public BaseSelfEntity() {
    }



}
