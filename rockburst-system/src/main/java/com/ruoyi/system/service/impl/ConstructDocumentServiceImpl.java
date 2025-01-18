package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.ConstructDocumentEntity;
import com.ruoyi.system.domain.SysFileInfo;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.mapper.ConstructDocumentMapper;
import com.ruoyi.system.service.ConstructDocumentService;
import com.ruoyi.system.service.SysFileInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2024/11/19
 * @description:
 */

@Transactional
@Service
public class ConstructDocumentServiceImpl extends ServiceImpl<ConstructDocumentMapper, ConstructDocumentEntity> implements ConstructDocumentService{

    @Resource
    private ConstructDocumentMapper constructDocumentMapper;

    @Resource
    private SysFileInfoService sysFileInfoService;

    /**
     * 新增层级
     * @param levelDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int insertLevel(LevelDTO levelDTO) {
        Long selectCount = constructDocumentMapper.selectCount(new LambdaQueryWrapper<ConstructDocumentEntity>()
                .eq(ConstructDocumentEntity::getDocumentName, levelDTO.getLevelName())
                .ne(ConstructDocumentEntity::getDelFlag, ConstantsInfo.TWO_DEL_FLAG));
        if (selectCount > 0) {
            throw new RuntimeException("层级名称已存在");
        }
        ConstructDocumentEntity constructDocumentEntity = new ConstructDocumentEntity();
         BeanUtils.copyProperties(levelDTO,constructDocumentEntity);
        long ts = System.currentTimeMillis();
        if (ObjectUtil.isNull(levelDTO.getSuperId())) {
            constructDocumentEntity.setSuperId(0L);
            constructDocumentEntity.setLevel(ConstantsInfo.LEVEL);
            constructDocumentEntity.setSuperId(null);
        } else {
            Long count = constructDocumentMapper.selectCount(new LambdaQueryWrapper<ConstructDocumentEntity>()
                    .eq(ConstructDocumentEntity::getSuperId, levelDTO.getSuperId()));
            if (count == 0) {
                constructDocumentEntity.setSort(1L);
            } else {
                constructDocumentEntity.setSort(count + 1);
            }
            ConstructDocumentEntity documentEntity = constructDocumentMapper.selectOne(new LambdaQueryWrapper<ConstructDocumentEntity>()
                    .eq(ConstructDocumentEntity::getDataId, levelDTO.getSuperId()));
            if (ObjectUtil.isNotNull(documentEntity)) {
                constructDocumentEntity.setLevel(documentEntity.getLevel() + ConstantsInfo.LEVEL);
                constructDocumentEntity.setSuperId(documentEntity.getDataId());
            }
        }
        constructDocumentEntity.setCreateBy(SecurityUtils.getUserId());
        constructDocumentEntity.setCreateTime(ts);
        constructDocumentEntity.setTag(ConstantsInfo.THREE_TAG);
        constructDocumentEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        return constructDocumentMapper.insert(constructDocumentEntity);
    }

    /**
     * 修改层级
     * @param levelDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int updateLevel(LevelDTO levelDTO) {
        if (ObjectUtil.isEmpty(levelDTO.getDataId())) {
            throw new RuntimeException("主键id不能为空");
        }
        ConstructDocumentEntity documentEntity = constructDocumentMapper.selectOne(new LambdaQueryWrapper<ConstructDocumentEntity>()
                .eq(ConstructDocumentEntity::getDataId, levelDTO.getDataId())
                .eq(ConstructDocumentEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNotNull(documentEntity)) {
            throw new RuntimeException("未找到此层级");
        }
        ConstructDocumentEntity entity = constructDocumentMapper.selectOne(new LambdaQueryWrapper<ConstructDocumentEntity>()
                .eq(ConstructDocumentEntity::getDocumentName, levelDTO.getLevelName())
                .eq(ConstructDocumentEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                .ne(ConstructDocumentEntity::getDataId, levelDTO.getDataId()));
        if (ObjectUtil.isNotNull(entity)) {
            throw new RuntimeException("层级名称已存在");
        }
        ConstructDocumentEntity constructDocumentEntity = new ConstructDocumentEntity();
        BeanUtils.copyProperties(levelDTO,constructDocumentEntity);
        long ts = System.currentTimeMillis();
        Long dataId = documentEntity.getDataId();
        Long sort = documentEntity.getSort();
        Integer level = documentEntity.getLevel();
        constructDocumentEntity.setDataId(dataId);
        constructDocumentEntity.setSort(sort);
        constructDocumentEntity.setLevel(level);
        constructDocumentEntity.setUpdateBy(1L);
        constructDocumentEntity.setUpdateTime(ts);
        constructDocumentEntity.setTag(documentEntity.getTag());
        constructDocumentEntity.setDelFlag(documentEntity.getDelFlag());
        return constructDocumentMapper.updateById(constructDocumentEntity);
    }


    /**
     * 文件上传
     * @param file 文件
     * @param bucketName 桶的名称
     * @param dataId 层级id
     * @return 返回结果
     */
    @Override
    public int addFile(MultipartFile file, String bucketName, Long dataId) {
        if (ObjectUtil.isEmpty(file)) {
            throw new RuntimeException("文件不能为空");
        }
        if (ObjectUtil.isEmpty(bucketName)) {
            throw new RuntimeException("存储桶名称不能为空");
        }
        int flag = 0;
        try {
              SysFileInfo upload = sysFileInfoService.upload(file, bucketName, "0");
              if (ObjectUtil.isNotEmpty(upload)) {
                   ConstructDocumentEntity constructDocumentEntity = new ConstructDocumentEntity();
                   long ts = System.currentTimeMillis();
                   constructDocumentEntity.setDocumentName(upload.getFileOldName());
                   constructDocumentEntity.setFileId(upload.getFileId());
                  constructDocumentEntity.setCreateTime(ts);
                  constructDocumentEntity.setCreateBy(1L);
                   constructDocumentEntity.setUpdateBy(1L);
                   constructDocumentEntity.setUpdateTime(ts);
                   constructDocumentEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
                   if (ObjectUtil.isNull(dataId)) {
                       constructDocumentEntity.setLevel(ConstantsInfo.LEVEL);
                       constructDocumentEntity.setSort(1L);
                       constructDocumentEntity.setTag(ConstantsInfo.ONE_TAG);

                       flag = constructDocumentMapper.insert(constructDocumentEntity);
                       if (flag <= 0) {
                           throw new RuntimeException("施工文档添加失败");
                       }
                   } else {
                       Long count = constructDocumentMapper.selectCount(new LambdaQueryWrapper<ConstructDocumentEntity>()
                               .eq(ConstructDocumentEntity::getSuperId, dataId));
                       ConstructDocumentEntity documentEntity = constructDocumentMapper.selectOne(new LambdaQueryWrapper<ConstructDocumentEntity>()
                               .eq(ConstructDocumentEntity::getDataId, dataId));
                       constructDocumentEntity.setSuperId(dataId);
                       constructDocumentEntity.setLevel(documentEntity.getLevel() + ConstantsInfo.LEVEL);
                       constructDocumentEntity.setSort(count + 1);
                       constructDocumentEntity.setTag(ConstantsInfo.TWO_TAG);
                       flag = constructDocumentMapper.insert(constructDocumentEntity);
                       if (flag <= 0) {
                           throw new RuntimeException("施工文档添加失败");
                       }
                   }
              }
        } catch (Exception e) {
            throw new RuntimeException("施工文档添加失败", e);
        }
        return flag;
    }

    /**
     * 文档修改
     * @param file 文件
     * @param bucketName 桶的名称
     * @param dataId id
     * @param fileIds 文件id数组
     * @param documentName 文档名称
     * @return 返回结果
     */
    @Override
    public int updateFile(MultipartFile file, String bucketName, Long dataId, Long[] fileIds, String documentName) {
        if (ObjectUtil.isEmpty(dataId)) {
            throw new RuntimeException("主键id不能为空");
        }
        if (ObjectUtil.isEmpty(file)) {
            throw new RuntimeException("文件不能为空");
        }
        if (ObjectUtil.isEmpty(bucketName)) {
            throw new RuntimeException("存储桶名称不能为空");
        }
        ConstructDocumentEntity documentEntity = constructDocumentMapper.selectOne(new LambdaQueryWrapper<ConstructDocumentEntity>()
                .eq(ConstructDocumentEntity::getDataId, dataId)
                .eq(ConstructDocumentEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(documentEntity)) {
            throw new RuntimeException("未找到此文档");
        }
        int flag = 0;
        String fileName = "";
        try {
            sysFileInfoService.batchLogicalDelete(fileIds);
            SysFileInfo upload = sysFileInfoService.upload(file, bucketName, "0");
            ConstructDocumentEntity constructDocumentEntity = new ConstructDocumentEntity();
            if (ObjectUtil.isNotEmpty(upload)) {
                BeanUtils.copyProperties(documentEntity,constructDocumentEntity);
                if (StringUtils.isNotEmpty(documentName)) {
                    Long selectCount = constructDocumentMapper.selectCount(new LambdaQueryWrapper<ConstructDocumentEntity>()
                            .eq(ConstructDocumentEntity::getDocumentName, documentName)
                            .eq(ConstructDocumentEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                            .ne(ConstructDocumentEntity::getDataId, dataId));
                    if (selectCount > 0) {
                        throw new RuntimeException("文档名称已存在");
                    }
                    fileName = documentName;
                }
                fileName = upload.getFileOldName();
                constructDocumentEntity.setDocumentName(fileName);
                constructDocumentEntity.setFileId(upload.getFileId());
                documentEntity.setUpdateBy(1L);
                constructDocumentEntity.setUpdateTime(System.currentTimeMillis());
                flag = constructDocumentMapper.updateById(constructDocumentEntity);
                if (flag <= 0) {
                    throw new RuntimeException("施工文档编辑失败");
                }
            }
        }catch (Exception e) {
            throw new RuntimeException("施工文档编辑失败", e);
        }
        return flag;
    }


    /**
     * 分页查询数据
     * @param selectDocumentDTO 查询参数DTO
     * @param pageNum 分页参数
     * @param pageSize 分页参数
     * @return 返回结果
     */
    @Override
    public TableData queryByPage(SelectDocumentDTO selectDocumentDTO, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<ConstructDocumentEntity> page = constructDocumentMapper.queryByPage(selectDocumentDTO);
        List<DocumentTreeDTO> documentTreeDTOS = new ArrayList<>();
        page.getResult().stream().filter(constructDocumentEntity -> constructDocumentEntity.getSuperId() == null)
                .peek(constructDocumentEntity -> documentTreeDTOS.add(new DocumentTreeDTO(constructDocumentEntity.getLabel(),
                        String.valueOf(constructDocumentEntity.getValue()), String.valueOf(constructDocumentEntity.documentName()),
                        constructDocumentEntity.getFileId(), constructDocumentEntity.getCreateTime(),constructDocumentEntity.getLevel(),
                        constructDocumentEntity.getSort(), constructDocumentEntity.isDisable(),
                        DocumentTreeDTO.treeRecurrence(constructDocumentEntity.getValue(), page.getResult()))))
                .collect(Collectors.toList());
        // 文件名称进行筛选
        if (selectDocumentDTO.getDocumentName() != null && !selectDocumentDTO.getDocumentName().isEmpty()) {
            treeMatch(documentTreeDTOS, selectDocumentDTO.getDocumentName());
        }
        Set<DocumentTreeDTO> set = new HashSet<>(documentTreeDTOS);
        TreeSet<DocumentTreeDTO> sortSet = new TreeSet<>(new Comparator<DocumentTreeDTO>() {
            @Override
            public int compare(DocumentTreeDTO o1, DocumentTreeDTO o2) {
             return Integer.parseInt(o2.getValue()) - Integer.parseInt(o1.getValue());
            }
        });
        sortSet.addAll(set);
        List<DocumentTreeDTO> list = new ArrayList<DocumentTreeDTO>(sortSet);
        result.setTotal(list.size());
        result.setRows(list);
        return result;
    }

       /**
     * 获取上级层级名称下拉列表
     * @return 返回结果
     */
    @Override
    public List<DropDownListDTO> getDropDownList() {
        return constructDocumentMapper.selectDropDownList();
    }

    /**
     * 上移/下移
     * @param adjustOrderDTO 参数
     * @return 返回结果
     */
    @Override
    public int moveOrder(AdjustOrderDTO adjustOrderDTO) {
        ConstructDocumentEntity documentEntity = constructDocumentMapper.selectOne(new LambdaQueryWrapper<ConstructDocumentEntity>()
                .eq(ConstructDocumentEntity::getDataId, adjustOrderDTO.getDataId())
                .eq(ConstructDocumentEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isEmpty(documentEntity)) {
            return -2;
        }
        long ts = System.currentTimeMillis();
        // 上移
        if (adjustOrderDTO.getOperaType().equals(1)) {
            ConstructDocumentEntity previousRe = constructDocumentMapper.selectOne(new LambdaQueryWrapper<ConstructDocumentEntity>()
                .eq(ConstructDocumentEntity::getSort, documentEntity.getSort() - 1)
                .eq(ConstructDocumentEntity::getSuperId, documentEntity.getSuperId()));
            // 更新上一条记录 sort 为当前值
            previousRe.setSort(previousRe.getSort() + 1);
            previousRe.setUpdateTime(ts);
            previousRe.setUpdateBy(1L); //SecurityUtils.getUserId()
            constructDocumentMapper.updateById(previousRe);
            //更新当前记录的 sort 为上一条
            documentEntity.setSort(documentEntity.getSort() - 1);
            documentEntity.setUpdateTime(ts);
            documentEntity.setUpdateBy(1L);//SecurityUtils.getUserId()
            constructDocumentMapper.updateById(documentEntity);
        }
        //下移
        if (adjustOrderDTO.getOperaType().equals(2)) {
            ConstructDocumentEntity laterRe = constructDocumentMapper.selectOne(new LambdaQueryWrapper<ConstructDocumentEntity>()
                .eq(ConstructDocumentEntity::getSort, documentEntity.getSort() + 1)
                .eq(ConstructDocumentEntity::getSuperId, documentEntity.getSuperId()));
             //更新下一条记录的 sort 为当前值
            laterRe.setSort(laterRe.getSort() - 1);
            laterRe.setUpdateTime(ts);
            laterRe.setUpdateBy(1L); //SecurityUtils.getUserId()
            constructDocumentMapper.updateById(laterRe);
            //更新当前记录的 sort 为下一条
            documentEntity.setSort(documentEntity.getSort() + 1);
            documentEntity.setUpdateTime(ts);
            documentEntity.setUpdateBy(1L); //SecurityUtils.getUserId()
            constructDocumentMapper.updateById(documentEntity);
        }
        return 1;
    }

    /**
     * 删除数据
     * @param dataId dataId
     * @return 返回结果
     */
    @Override
    public boolean deleteByDataId(Long dataId) {
        if (ObjectUtil.isNull(dataId)) {
            throw new RuntimeException("参数错误,请选择数据！！");
        }
        ConstructDocumentEntity documentEntity = constructDocumentMapper.selectOne(new LambdaQueryWrapper<ConstructDocumentEntity>()
                .eq(ConstructDocumentEntity::getDataId, dataId)
                .eq(ConstructDocumentEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(documentEntity)) {
            throw new RuntimeException("未找到id为" + dataId + "的数据");
        }
        Long selectCount = constructDocumentMapper.selectCount(new LambdaQueryWrapper<ConstructDocumentEntity>()
                .eq(ConstructDocumentEntity::getSuperId, dataId)
                .eq(ConstructDocumentEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (selectCount > 0) {
            throw new RuntimeException("删除失败！存在子集数据，请先删除子节点数据！");
        }
        int update = constructDocumentMapper.deleteById(dataId);
        if (update > 0) {
            return true;
        }
        return false;
    }

    /**
     * 文件名称进行筛选
     */
    private List<DocumentTreeDTO> treeMatch(List<DocumentTreeDTO> documentTreeDTOS, String documentName) {
        Iterator<DocumentTreeDTO> iterator = documentTreeDTOS.iterator();
        while (iterator.hasNext()) {
            DocumentTreeDTO documentTreeDTO = iterator.next();
            if (!documentTreeDTO.getDocumentName().contains(documentName)) {
                if (!CollectionUtils.isEmpty(documentTreeDTO.getChildren())) {
                    documentTreeDTO.setChildren(treeMatch(documentTreeDTO.getChildren(), documentName));
                    if (CollectionUtils.isEmpty(documentTreeDTO.getChildren())) {
                        iterator.remove();
                    }
                } else {
                    iterator.remove();
                }
            }
        }
        return documentTreeDTOS;
    }
}