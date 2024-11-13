package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.SurveyAreaEntity;
import com.ruoyi.system.domain.dto.SurveyAreaDTO;
import com.ruoyi.system.domain.dto.SurveySelectDTO;
import com.ruoyi.system.domain.vo.SurveyAreaVO;
import com.ruoyi.system.mapper.SurveyAreaMapper;
import com.ruoyi.system.service.SurveyAreaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/11
 * @description:
 */
@Service
@Transactional
public class SurveyAreaServiceImpl extends ServiceImpl<SurveyAreaMapper, SurveyAreaEntity> implements SurveyAreaService {

    @Resource
    private SurveyAreaMapper surveyAreaMapper;

    /**
     * 新增测区
     * @param surveyAreaDTO 参数接收DTO
     * @return 返回结果
     */
    @Override
    public SurveyAreaDTO insertSurveyArea(SurveyAreaDTO surveyAreaDTO) {
        LambdaQueryWrapper<SurveyAreaEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SurveyAreaEntity::getSurveyAreaName,surveyAreaDTO.getSurveyAreaName());
        Long selectCount = surveyAreaMapper.selectCount(queryWrapper);
        if (selectCount > 0) {
            throw new RuntimeException("测区名称不能重复！");
        }
        SurveyAreaEntity surveyAreaEntity = new SurveyAreaEntity();
        BeanUtils.copyProperties(surveyAreaDTO,surveyAreaEntity);
        long ts = System.currentTimeMillis();
        surveyAreaEntity.setCreateTime(ts);
        surveyAreaEntity.setUpdateTime(ts);
        // TODO: 2024/11/11 系统暂时去掉token,最后统一做鉴权；userId会从token取
        surveyAreaEntity.setCreateBy(1L);
        surveyAreaEntity.setUpdateBy(1L);
        surveyAreaMapper.insert(surveyAreaEntity);
        BeanUtils.copyProperties(surveyAreaEntity,surveyAreaDTO);
        return surveyAreaDTO;
    }

    /**
     * 测区编辑
     * @param surveyAreaDTO 参数接收DTO
     * @return 返回结果
     */
    @Override
    public SurveyAreaDTO updateSurveyArea(SurveyAreaDTO surveyAreaDTO) {
        if (ObjectUtil.isEmpty(surveyAreaDTO.getSurveyAreaId())) {
            throw new ServiceException("测区id不能为空!");
        }
        SurveyAreaEntity surveyAreaEntity = surveyAreaMapper.selectById(surveyAreaDTO.getMiningAreaId());
        if (ObjectUtil.isNotEmpty(surveyAreaEntity)) {
            throw new ServiceException("测区不存在!");
        }
        LambdaQueryWrapper<SurveyAreaEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SurveyAreaEntity::getSurveyAreaName,surveyAreaDTO.getSurveyAreaName())
                .ne(SurveyAreaEntity::getSurveyAreaId,surveyAreaDTO.getSurveyAreaId());
        Long selectCount = surveyAreaMapper.selectCount(queryWrapper);
        if (selectCount > 0) {
            throw new RuntimeException("测区名称不能重复！");
        }
        Long surveyAreaId = surveyAreaEntity.getSurveyAreaId();
        BeanUtils.copyProperties(surveyAreaDTO,surveyAreaEntity);
        surveyAreaEntity.setSurveyAreaId(surveyAreaId);
        surveyAreaEntity.setUpdateTime(System.currentTimeMillis());
        surveyAreaEntity.setUpdateBy(1L);
        surveyAreaMapper.updateById(surveyAreaEntity);
        BeanUtils.copyProperties(surveyAreaEntity,surveyAreaDTO);
        return surveyAreaDTO;
    }

    /**
     * 查询详情
     * @param surveyAreaId 测区id
     * @return 返回查询
     */
    @Override
    public SurveyAreaDTO getSurveyAreaById(Long surveyAreaId) {
        LambdaQueryWrapper<SurveyAreaEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SurveyAreaEntity::getSurveyAreaId,surveyAreaId);
        SurveyAreaEntity surveyAreaEntity = surveyAreaMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(surveyAreaEntity)) {
            throw new ServiceException("未找到此数据!");
        }
        SurveyAreaDTO surveyAreaDTO = new SurveyAreaDTO();
        BeanUtils.copyProperties(surveyAreaEntity,surveyAreaDTO);
        return surveyAreaDTO;
    }

    @Override
    public TableData pageQueryList(SurveySelectDTO surveySelectDTO, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<SurveyAreaVO> page = surveyAreaMapper.selectSurveyAreaByPage(surveySelectDTO);
        if (ListUtils.isNotNull(page.getResult())) {
            page.getResult().forEach(surveyAreaVO -> {
                // todo: 后续优化
                surveyAreaVO.setWorkFaceNameFmt("");
                surveyAreaVO.setMiningAreaNameFmt("");
                surveyAreaVO.setTunnelNameFmt("");
                surveyAreaVO.setCreatTimeFmt(DateUtils.getDateStrByTime(surveyAreaVO.getCreatTime()));
            });
        }
        result.setTotal(page.getTotal());
        result.setRows(page.getResult());
        return result;
    }

    /**
     * 批量删除测区
     * @param surveyAreaIds 测区id
     * @return 返回
     */
    @Override
    public boolean deleteSurveyArea(Long[] surveyAreaIds) {
        boolean flag = false;//删除标识
        if (surveyAreaIds.length == 0) {
            throw new ServiceException("请选择要删除的数据!");
        }
        List<Long> ids = Arrays.asList(surveyAreaIds);
        flag = this.removeBatchByIds(ids);
        return flag;
    }
}