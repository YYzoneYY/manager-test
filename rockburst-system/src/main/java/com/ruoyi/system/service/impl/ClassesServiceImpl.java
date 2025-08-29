package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.Entity.ClassesEntity;
import com.ruoyi.system.domain.dto.ClassesChoiceListDTO;
import com.ruoyi.system.domain.dto.ClassesSelectDTO;
import com.ruoyi.system.domain.vo.ClassesVO;
import com.ruoyi.system.mapper.ClassesMapper;
import com.ruoyi.system.service.ClassesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2024/11/16
 * @description:
 */

@Transactional
@Service
public class ClassesServiceImpl extends ServiceImpl<ClassesMapper, ClassesEntity> implements ClassesService{

    @Resource
    private ClassesMapper classesMapper;

    /**
     * 新增班次
     * @param classesEntity 参数实体类
     * @return 返回结果
     */
    @Override
    public int insertClasses(ClassesEntity classesEntity, Long mineId) {
        Long selectCount = classesMapper.selectCount(new LambdaQueryWrapper<ClassesEntity>()
                .eq(ClassesEntity::getClassesName, classesEntity.getClassesName())
                .eq(ClassesEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                .eq(ClassesEntity::getMineId, mineId));
        if (selectCount > 0) {
            throw new RuntimeException("班次名称已存在");
        }
        long ts = System.currentTimeMillis();
        classesEntity.setCreateTime(ts);
        classesEntity.setCreateBy(SecurityUtils.getUserId());
        classesEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        classesEntity.setMineId(mineId);
        return classesMapper.insert(classesEntity);
    }

    /**
     * 修改班次
     * @param classesEntity 参数实体类
     * @return 返回结果
     */
    @Override
    public int updateClasses(ClassesEntity classesEntity, Long mineId) {
        if (ObjectUtil.isEmpty(classesEntity.getClassesId())) {
            throw new RuntimeException("班次id不能为空");
        }
        ClassesEntity selectClassesEntity = classesMapper.selectOne(new LambdaQueryWrapper<ClassesEntity>()
                .eq(ClassesEntity::getClassesId, classesEntity.getClassesId())
                .eq(ClassesEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isEmpty(selectClassesEntity)) {
            throw new RuntimeException("班次不存在");
        }
        Long selectCount = classesMapper.selectCount(new LambdaQueryWrapper<ClassesEntity>()
                .eq(ClassesEntity::getClassesName, classesEntity.getClassesName())
                .eq(ClassesEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                .eq(ClassesEntity::getMineId, mineId)
                .ne(ClassesEntity::getClassesId, classesEntity.getClassesId()));
        if (selectCount > 0) {
            throw new RuntimeException("班次名称已存在");
        }
        Long classesId = classesEntity.getClassesId();
        classesEntity.setClassesId(classesId);
        classesEntity.setUpdateTime(System.currentTimeMillis());
        classesEntity.setUpdateBy(SecurityUtils.getUserId());
        return classesMapper.updateById(classesEntity);
    }

    /**
     * 根据id查询
     * @param classesId 班次id
     * @return 返回结果
     */
    @Override
    public ClassesEntity getClassesById(Long classesId) {
        ClassesEntity selectClassesEntity = classesMapper.selectOne(new LambdaQueryWrapper<ClassesEntity>()
                .eq(ClassesEntity::getClassesId, classesId)
                .eq(ClassesEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isEmpty(selectClassesEntity)) {
            throw new RuntimeException("班次不存在");
        }
        return selectClassesEntity;
    }

    /**
     * 分页查询
     * @param classesSelectDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    @Override
    public TableData pageQueryList(ClassesSelectDTO classesSelectDTO, Integer pageNum, Integer pageSize, Long mineId) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<ClassesVO> page = classesMapper.selectClassesList(classesSelectDTO, mineId);
        if (ListUtils.isNotNull(page.getResult())) {
            page.getResult().forEach(classesVO -> {
                classesVO.setCreateTimeFmt(DateUtils.getDateStrByTime(classesVO.getCreateTime()));
                classesVO.setUpdateTimeFmt(DateUtils.getDateStrByTime(classesVO.getUpdateTime()));
            });
        }
        result.setTotal(page.getTotal());
        result.setRows(page.getResult());
        return result;
    }

    /**
     * 批量删除
     * @param classesIds 主键id数组
     * @return 返回结果
     */
    @Override
    public boolean deleteClasses(Long[] classesIds) {
        boolean flag = false;
        if (classesIds.length == 0) {
            throw new ServiceException("请选择要删除的数据!");
        }
        List<Long> ids = Arrays.asList(classesIds);
        flag = this.removeBatchByIds(ids);
        return flag;
    }

    /**
     * 获取班次下拉列表
     * @return 返回结果
     */
    @Override
    public List<ClassesChoiceListDTO> getClassesChoiceList() {
        List<ClassesChoiceListDTO> classesChoiceListDTOS = new ArrayList<>();
        List<ClassesEntity> classesEntities = classesMapper.selectList(new LambdaQueryWrapper<ClassesEntity>()
                .ne(ClassesEntity::getDelFlag, ConstantsInfo.TWO_DEL_FLAG));
        if (ListUtils.isNotNull(classesEntities)) {
            classesChoiceListDTOS = classesEntities.stream().map(classesEntity -> {
                ClassesChoiceListDTO classesChoiceListDTO = new ClassesChoiceListDTO();
                classesChoiceListDTO.setLabel(classesEntity.getClassesName());
                classesChoiceListDTO.setValue(classesEntity.getClassesId());
                return classesChoiceListDTO;
            }).collect(Collectors.toList());
        }
        return classesChoiceListDTOS;
    }
}