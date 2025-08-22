package com.ruoyi.system.service;

import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.dto.actual.ActualDTO;
import com.ruoyi.system.domain.dto.actual.ActualSelectDTO;
import com.ruoyi.system.domain.dto.actual.ActualSelectTDTO;
import com.ruoyi.system.domain.dto.actual.LineGraphDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/8/13
 * @description:
 */
public interface MeasureActualService {

    Boolean createIndex();

    int insert(ActualDTO actualDTO, Long mineId);

    TableData ActualDataPage(ActualSelectDTO actualSelectDTO, List<String> sensorTypes, Long mineId,
                                    String tag, Integer pageNum, Integer pageSize);

    TableData ActualDataTPage(ActualSelectTDTO actualSelectTDTO, List<String> sensorTypes, Long mineId, Integer pageNum, Integer pageSize);

    List<LineGraphDTO> obtainLineGraph(String measureNum, String range, Long  startTime, Long endTime,
                                       List<String> sensorTypes, Long mineId);
}