package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门表 sys_dept
 * 
 * @author ruoyi
 */
@Getter
@Setter
public class SysCompany extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    /** 部门ID */
    @TableId()
    private Long companyId;

    /** 父部门ID */
    private String mineIds;

    /** 父部门ID */
    private Long parentId;

    /** 祖级列表 */
    private String ancestors;

    /** 部门名称 */
    private String companyName;


    /** 部门名称 */
    @TableField(exist = false)
    private String mineName;


    /** 显示顺序 */
    private Integer orderNum;

    /** 负责人 */
    private String leader;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 部门状态:0正常,1停用 */
    private String status;



//    /** 父部门名称 */
//    private String parentName;

    /** 施工单位 */
//    private Long constructionUnitId;
    
//    /** 子部门 */
//    private List<SysCompany> children = new ArrayList<SysCompany>();




}
