package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.ConstructDocumentEntity;
import com.ruoyi.system.domain.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/19
 * @description:
 */
public interface ConstructDocumentService extends IService<ConstructDocumentEntity> {

    /**
     * 新增层级
     * @param levelDTO 参数DTO
     * @return 返回结果
     */
    int insertLevel(LevelDTO levelDTO);

    /**
     * 修改层级
     * @param levelDTO 参数DTO
     * @return 返回结果
     */
    int updateLevel(LevelDTO levelDTO);

    /**
     * 施工文档上传
     * @param constructFIleDTO 参数DTO
     * @return 返回结果
     */
    int addFile(ConstructFIleDTO constructFIleDTO, MultipartFile file, String bucketName);

    /**
     * 施工文档修改
     * @param constructFIleDTO 参数DTO
     * @return 返回结果
     */
    int updateFile(ConstructFIleDTO constructFIleDTO, MultipartFile file, String bucketName);

    /**
     * 分页查询数据
     * @param selectDocumentDTO 查询参数DTO
     * @param pageNum 分页参数
     * @param pageSize 分页参数
     * @return 返回结果
     */
    TableData queryByPage(SelectDocumentDTO selectDocumentDTO, Integer pageNum, Integer pageSize);

    /**
     * 获取上级层级名称下拉列表
     * @return 返回结果
     */
    List<DropDownListDTO> getDropDownList();

    /**
     * 上移/下移
     * @param adjustOrderDTO 参数
     * @return 返回结果
     */
    int moveOrder(AdjustOrderDTO adjustOrderDTO);

}