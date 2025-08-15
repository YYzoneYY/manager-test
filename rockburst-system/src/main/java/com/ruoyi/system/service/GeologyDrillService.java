package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.GeologyDrillEntity;
import com.ruoyi.system.domain.dto.GeologyDrillDTO;
import com.ruoyi.system.domain.dto.GeologyDrillInfoDTO;
import com.ruoyi.system.domain.vo.GeologyDrillVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/6/17
 * @description:
 */
public interface GeologyDrillService extends IService<GeologyDrillEntity> {

    boolean batchInsert(List<GeologyDrillDTO> geologyDrillDTOList);

    GeologyDrillInfoDTO obtainGeologyDrillInfo(String drillName);

    TableData pageQueryList(String drillName, Integer pageNum, Integer pageSize);

    List<GeologyDrillVO> obtainGeologyDrillList(Long mineId);

    /**
     * 批量导入
     * @param file 文件
     * @return 返回结果
     * @throws Exception
     */
    String importData(MultipartFile file, Long geologyDrillId) throws Exception;

    /**
     * 一键删除
     * @return 删除结果
     */
    boolean oneClickDelete();
}