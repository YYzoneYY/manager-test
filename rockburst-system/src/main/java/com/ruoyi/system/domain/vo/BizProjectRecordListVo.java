package com.ruoyi.system.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.system.domain.BizMine;
import com.ruoyi.system.domain.BizProjectRecord;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BizProjectRecordListVo extends BizProjectRecord {

    private String constructUnitName;
    private String constructShiftName;
    private String constructLocationName;



}
