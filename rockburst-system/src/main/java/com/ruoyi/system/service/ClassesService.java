package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.ClassesEntity;
import com.ruoyi.system.domain.dto.ClassesChoiceListDTO;
import com.ruoyi.system.domain.dto.ClassesSelectDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/16
 * @description:
 */
public interface ClassesService extends IService<ClassesEntity> {

    /**
     * 新增班次
     * @param classesEntity 参数实体类
     * @return 返回结果
     */
    int insertClasses(ClassesEntity classesEntity);

    /**
     * 修改班次
     * @param classesEntity 参数实体类
     * @return 返回结果
     */
    int updateClasses(ClassesEntity classesEntity);

    /**
     * 根据id查询
     * @param classesId 班次id
     * @return 返回结果
     */
    ClassesEntity getClassesById(Long classesId);

    /**
     * 分页查询
     * @param classesSelectDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    TableData pageQueryList(ClassesSelectDTO classesSelectDTO, Integer pageNum, Integer pageSize);

    /**
     * 批量删除
     * @param classesIds 主键id数组
     * @return 返回结果
     */
    boolean deleteClasses(Long[] classesIds);

    /**
     * 获取班次下拉列表
     * @return 返回结果
     */
    List<ClassesChoiceListDTO> getClassesChoiceList();

}