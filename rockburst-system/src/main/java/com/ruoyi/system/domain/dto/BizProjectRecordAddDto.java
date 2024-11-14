package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.BizDrillRecord;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizVideo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BizProjectRecordAddDto extends BizProjectRecord {




    List<BizDrillRecord> drillRecords;

    List<BizVideo>  videos;

}
