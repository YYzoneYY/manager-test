package com.ruoyi.system.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import com.ruoyi.system.domain.BizMiningArea;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 采区管理对象 biz_mining_area
 * 
 * @author ruoyi
 * @date 2024-11-11
 */

@Getter
@Setter
public class BizMiningAreaVo extends BizMiningArea
{
    private static final long serialVersionUID = 1L;


    private String mineName;

}
