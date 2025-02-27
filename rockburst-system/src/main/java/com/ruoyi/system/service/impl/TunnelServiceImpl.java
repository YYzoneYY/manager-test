package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.RelatesInfoEntity;
import com.ruoyi.system.domain.Entity.SurveyAreaEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.SelectTunnelDTO;
import com.ruoyi.system.domain.dto.TunnelChoiceListDTO;
import com.ruoyi.system.domain.dto.TunnelDTO;
import com.ruoyi.system.domain.vo.TunnelVO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.TunnelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2024/11/21
 * @description:
 */
@Service
@Transactional
public class TunnelServiceImpl extends ServiceImpl<TunnelMapper, TunnelEntity> implements TunnelService {

    @Resource
    private TunnelMapper tunnelMapper;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private SysDictDataMapper sysDictDataMapper;

    @Resource
    private RelatesInfoMapper relatesInfoMapper;

    @Resource
    private SurveyAreaMapper surveyAreaMapper;

    @Resource
    private BizProjectRecordMapper bizProjectRecordMapper;

    /**
     * 新增巷道
     * @param tunnelDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int insertTunnel(TunnelDTO tunnelDTO) {
        int flag = 0;
        Long selectCount = tunnelMapper.selectCount(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getTunnelName, tunnelDTO.getTunnelName())
                .eq(TunnelEntity::getWorkFaceId, tunnelDTO.getWorkFaceId())
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (selectCount > 0) {
            throw new RuntimeException("巷道名称已存在");
        }
        if (tunnelDTO.getTunnelType().equals(ConstantsInfo.UPPER_TUNNEL) ||
                tunnelDTO.getTunnelType().equals(ConstantsInfo.BELOW_TUNNEL) || tunnelDTO.getTunnelType().equals(ConstantsInfo.OPEN_OFF_CUT)) {
            TunnelEntity tunnel = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                    .eq(TunnelEntity::getWorkFaceId, tunnelDTO.getWorkFaceId())
                    .eq(TunnelEntity::getTunnelType, tunnelDTO.getTunnelType())
                    .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNotNull(tunnel)) {
                throw new RuntimeException("此工作面已存在类型为：" + tunnel.getTunnelType() + "的巷道");
            }
        }
        tunnelDTO.setCreateTime(System.currentTimeMillis());
        tunnelDTO.setCreateBy(SecurityUtils.getUserId());
        TunnelEntity tunnelEntity = new TunnelEntity();
        BeanUtils.copyProperties(tunnelDTO, tunnelEntity);
        flag = tunnelMapper.insert(tunnelEntity);
        if (flag <= 0) {
            throw new RuntimeException("新增巷道失败");
        }
        return flag;
    }

    /**
     * 巷道编辑
     * @param tunnelDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int updateTunnel(TunnelDTO tunnelDTO) {
        int flag = 0;
        TunnelEntity tunnelEntity = tunnelMapper.selectById(tunnelDTO.getTunnelId());
        if (ObjectUtil.isNull(tunnelEntity)) {
            throw new RuntimeException("未找到此巷道");
        }
        Long selectCount = tunnelMapper.selectCount(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getTunnelName, tunnelDTO.getTunnelName())
                .eq(TunnelEntity::getWorkFaceId, tunnelDTO.getWorkFaceId())
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                .ne(TunnelEntity::getTunnelId, tunnelDTO.getTunnelId()));
        if (selectCount > 0) {
            throw new RuntimeException("巷道名称已存在");
        }
        if (tunnelDTO.getTunnelType().equals(ConstantsInfo.UPPER_TUNNEL) ||
                tunnelDTO.getTunnelType().equals(ConstantsInfo.BELOW_TUNNEL) || tunnelDTO.getTunnelType().equals(ConstantsInfo.OPEN_OFF_CUT)) {
            TunnelEntity tunnel = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                    .eq(TunnelEntity::getWorkFaceId, tunnelDTO.getWorkFaceId())
                    .eq(TunnelEntity::getTunnelType, tunnelDTO.getTunnelType())
                    .ne(TunnelEntity::getTunnelId, tunnelDTO.getTunnelId())
                    .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNotNull(tunnel)) {
                throw new RuntimeException("此工作面已存在类型为：" + tunnel.getTunnelType() + "的巷道");
            }
        }
        tunnelDTO.setUpdateTime(System.currentTimeMillis());
        tunnelDTO.setUpdateBy(SecurityUtils.getUserId());
        BeanUtils.copyProperties(tunnelDTO, tunnelEntity);
        flag = tunnelMapper.updateById(tunnelEntity);
        if (flag <= 0) {
            throw new RuntimeException("巷道修改失败");
        }
        return flag;
    }

    /**
     * 根据id查询
     * @param tunnelId 巷道id
     * @return 返回结果
     */
    @Override
    public TunnelDTO detail(Long tunnelId) {
        if (ObjectUtil.isNull(tunnelId)) {
            throw new RuntimeException("参数错误,主键不能为空");
        }
        TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getTunnelId, tunnelId)
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(tunnelEntity)) {
            throw new RuntimeException("未找到此巷道");
        }
        TunnelDTO tunnelDTO = new TunnelDTO();
        BeanUtils.copyProperties(tunnelEntity, tunnelDTO);
        String workFaceName = getWorkFaceName(tunnelEntity.getWorkFaceId());
        tunnelDTO.setWorkFaceName(workFaceName);
        String shape = sysDictDataMapper.selectDictLabel(ConstantsInfo.SECTION_SHAPE_DICT_TYPE, tunnelEntity.getSectionShape());
        String support = sysDictDataMapper.selectDictLabel(ConstantsInfo.SUPPORT_FORM_DICT_TYPE, tunnelEntity.getSupportForm());
        tunnelDTO.setSectionShapeFmt(shape);
        tunnelDTO.setSupportFormFmt(support);
        tunnelDTO.setCreateTimeFrm(tunnelEntity.getCreateTime() == null ? null : DateUtils.getDateStrByTime(tunnelEntity.getCreateTime()));
        tunnelDTO.setUpdateTimeFrm(tunnelEntity.getUpdateTime() == null ? null : DateUtils.getDateStrByTime(tunnelEntity.getUpdateTime()));
        return tunnelDTO;
    }

    @Override
    public TableData pageQueryList(SelectTunnelDTO selectTunnelDTO, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<TunnelVO> page = tunnelMapper.selectTunnelList(selectTunnelDTO);
        if (ListUtils.isNotNull(page.getResult())) {
            page.getResult().forEach(tunnelVO -> {
                tunnelVO.setWorkFaceName(getWorkFaceName(tunnelVO.getWorkFaceId()));
                String shape = sysDictDataMapper.selectDictLabel(ConstantsInfo.SECTION_SHAPE_DICT_TYPE, tunnelVO.getSectionShape());
                String support = sysDictDataMapper.selectDictLabel(ConstantsInfo.SUPPORT_FORM_DICT_TYPE, tunnelVO.getSupportForm());
                tunnelVO.setSectionShapeFmt(shape);
                tunnelVO.setSupportFormFmt(support);
                tunnelVO.setCreateTimeFrm(DateUtils.getDateStrByTime(tunnelVO.getCreateTime()));
            });
        }
        result.setTotal(page.getTotal());
        result.setRows(page.getResult());
        return result;
    }

    @Override
    public boolean deleteByIds(Long[] tunnelIds) {
        boolean flag = false;
        if (tunnelIds.length == 0) {
            throw new RuntimeException("请选择要删除的数据!");
        }
        List<Long> tunnelIdList = Arrays.asList(tunnelIds);
        tunnelIdList.forEach(tunnelId -> {
            Long selectCount = relatesInfoMapper.selectCount(new LambdaQueryWrapper<RelatesInfoEntity>()
                    .eq(RelatesInfoEntity::getPositionId, tunnelId)
                    .eq(RelatesInfoEntity::getType, ConstantsInfo.TUNNELING));
            if (selectCount > 0) {
                throw new RuntimeException("此巷道已关联到计划，无法删除!");
            }
            Long count = surveyAreaMapper.selectCount(new LambdaQueryWrapper<SurveyAreaEntity>()
                    .eq(SurveyAreaEntity::getTunnelId, tunnelId)
                    .eq(SurveyAreaEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (count > 0) {
                throw new RuntimeException("此巷道已关联到测区，无法删除!");
            }

            Long aLong = bizProjectRecordMapper.selectCount(new LambdaQueryWrapper<BizProjectRecord>()
                    .eq(BizProjectRecord::getConstructType, ConstantsInfo.TUNNELING)
                    .eq(BizProjectRecord::getLocationId, tunnelId));
            if (aLong > 0) {
                throw new RuntimeException("此巷道已关联到工程填报，无法删除!");
            }
            // TODO 关联顶板离层测点信息，巷道位移信息，后续追加
        });
        flag = this.removeBatchByIds(tunnelIdList);
        return flag;
    }

    /**
     * 获取巷道下拉框
     * @param faceId 工作面id
     * @return 返回结果
     */
    @Override
    public List<TunnelChoiceListDTO> getTunnelChoiceList(Long faceId) {
        List<TunnelChoiceListDTO> tunnelChoiceListDTOS = new ArrayList<>();
        if (ObjectUtil.isNull(faceId)) {
            throw new RuntimeException("工作面id不能为空!!");
        }
        List<TunnelEntity> tunnelEntities = tunnelMapper.selectList(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getWorkFaceId, faceId)
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ListUtils.isNotNull(tunnelEntities)) {
            tunnelChoiceListDTOS = tunnelEntities.stream().map(tunnelEntity -> {
                TunnelChoiceListDTO tunnelChoiceListDTO = new TunnelChoiceListDTO();
                tunnelChoiceListDTO.setLabel(tunnelEntity.getTunnelName());
                tunnelChoiceListDTO.setValue(tunnelEntity.getTunnelId());
                return tunnelChoiceListDTO;
            }).collect(Collectors.toList());
        }
        return tunnelChoiceListDTOS;
    }

    @Override
    public List<TunnelChoiceListDTO> getTunnelChoiceListTwo(Long faceId) {
        List<TunnelChoiceListDTO> tunnelChoiceListDTOS = new ArrayList<>();
        if (ObjectUtil.isNull(faceId)) {
            throw new RuntimeException("工作面id不能为空!!");
        }
        List<String> type = new ArrayList<>();
        type.add(ConstantsInfo.UPPER_TUNNEL);
        type.add(ConstantsInfo.BELOW_TUNNEL);
        List<TunnelEntity> tunnelEntities = tunnelMapper.selectList(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getWorkFaceId, faceId)
                .in(TunnelEntity::getTunnelType, type)
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ListUtils.isNotNull(tunnelEntities)) {
            tunnelChoiceListDTOS = tunnelEntities.stream().map(tunnelEntity -> {
                TunnelChoiceListDTO tunnelChoiceListDTO = new TunnelChoiceListDTO();
                tunnelChoiceListDTO.setLabel(tunnelEntity.getTunnelName());
                tunnelChoiceListDTO.setValue(tunnelEntity.getTunnelId());
                return tunnelChoiceListDTO;
            }).collect(Collectors.toList());
        }
        return tunnelChoiceListDTOS;
    }

    @Override
    public List<TunnelChoiceListDTO> getChoiceListFitterRule() {
        List<TunnelChoiceListDTO> tunnelChoiceListDTOS = new ArrayList<>();
        List<TunnelEntity> tunnelEntities = tunnelMapper.selectList(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ListUtils.isNotNull(tunnelEntities)) {
            tunnelChoiceListDTOS = tunnelEntities.stream().map(tunnelEntity -> {
                TunnelChoiceListDTO tunnelChoiceListDTO = new TunnelChoiceListDTO();
                tunnelChoiceListDTO.setLabel(tunnelEntity.getTunnelName());
                tunnelChoiceListDTO.setValue(tunnelEntity.getTunnelId());
                return tunnelChoiceListDTO;
            }).collect(Collectors.toList());
        }
        return tunnelChoiceListDTOS;
    }

    /**
     * 获取工作面名称
     */
    private String getWorkFaceName(Long workFaceId) {
        String workFaceName = null;
        BizWorkface bizWorkface = bizWorkfaceMapper.selectById(workFaceId);
        if (ObjectUtil.isNull(bizWorkface)) {
            return workFaceName;
        }
        workFaceName =  bizWorkface.getWorkfaceName();
        return workFaceName;
    }
}