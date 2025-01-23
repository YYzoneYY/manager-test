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
     * 文件上传
     * @param file 文件
     * @param bucketName 桶的名称
     * @param dataId 层级id
     * @return 返回结果
     */
    int addFile(MultipartFile file, String bucketName, Long dataId);

    /**
     * 文档修改
     * @param file 文件
     * @param bucketName 桶的名称
     * @param dataId id
     * @param fileIds 文件id数组
     * @param documentName 文档名称
     * @return 返回结果
     */
    int updateFile(MultipartFile file, String bucketName, Long dataId, Long[] fileIds, String documentName);

    /**
     * 分页查询数据
     * @param selectDocumentDTO 查询参数DTO
     * @param pageNum 分页参数
     * @param pageSize 分页参数
     * @return 返回结果
     */
    TableData queryByPage(SelectDocumentDTO selectDocumentDTO, Integer pageSize, Integer pageNum);

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

    /**
     * 删除数据
     * @param dataId dataId
     * @return 返回结果
     */
    boolean deleteByDataId(Long dataId);

    /**
     * 下载文件
     * @param fileId 文件id
     * @return 返回结果
     */
    String getFileUrl(Long fileId);

}