package com.ruoyi.system.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import com.ruoyi.system.domain.BizWorkface;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 工作面管理对象 biz_workface
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Getter
@Setter
public class BizWorkfaceVo extends BizWorkface
{
    private String mineName;

    private String miningAreaName;




}
