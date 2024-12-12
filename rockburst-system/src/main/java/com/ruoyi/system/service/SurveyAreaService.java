package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.SurveyAreaEntity;
import com.ruoyi.system.domain.dto.*;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/11
 * @description:
 */
public interface SurveyAreaService extends IService<SurveyAreaEntity> {

    /**
     * 新增测区
     * @param surveyAreaDTO 参数接收DTO
     * @return 返回结果
     */
    SurveyAreaDTO insertSurveyArea(SurveyAreaDTO surveyAreaDTO);

    /**
     * 测区编辑
     * @param surveyAreaDTO 参数接收DTO
     * @return 返回结果
     */
    SurveyAreaDTO updateSurveyArea(SurveyAreaDTO surveyAreaDTO);

    /**
     * 查询详情
     * @param surveyAreaId 测区id
     * @return 返回查询
     */
    SurveyAreaDTO getSurveyAreaById(Long surveyAreaId);

    /**
     * 分页查询
     * @param surveySelectDTO 查询参数DTO
     * @param pageNum 页数
     * @param pageSize 条数
     * @return 返回结果
     */
    TableData pageQueryList(SurveySelectDTO surveySelectDTO, Integer pageNum, Integer pageSize);

    /**
     * 批量删除测区
     * @param surveyAreaIds 测区id
     * @return 返回
     */
    boolean deleteSurveyArea(Long[] surveyAreaIds);

    /**
     * 获取采区下拉框
     * @return 返回结果
     */
    List<MiningAreaChoiceListDTO> getMiningAreaChoiceList();

    /**
     * 获取工作面下拉框
     * @param miningAreaId 采区id
     * @return 返回结果
     */
    List<FaceChoiceListDTO> getFaceChoiceList(Long miningAreaId);

    /**
     * 获取工作面下拉框
     * @param faceId 工作面id
     * @return 返回结果
     */
    List<TunnelChoiceListDTO> getTunnelChoiceList(Long faceId);
}