package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.DrillingStressEntity;
import com.ruoyi.system.domain.Entity.WarnSchemeEntity;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.utils.ConvertUtils;
import com.ruoyi.system.domain.utils.WholeSchemeUtils;
import com.ruoyi.system.domain.vo.WarnSchemeVO;
import com.ruoyi.system.mapper.BizWorkfaceMapper;
import com.ruoyi.system.mapper.WarnSchemeMapper;
import com.ruoyi.system.service.WarnSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2024/11/29
 * @description:
 */

@Service
@Transactional
public class WarnSchemeServiceImpl extends ServiceImpl<WarnSchemeMapper, WarnSchemeEntity> implements WarnSchemeService {

    @Resource
    private WarnSchemeMapper warnSchemeMapper;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Override
    public int addWarnScheme(WarnSchemeDTO warnSchemeDTO, Long mineId) {
        int flag = 0;
        if (ObjectUtil.isNull(warnSchemeDTO)) {
            throw new RuntimeException("参数错误,不能为空！");
        }
        if (ObjectUtil.isNull(warnSchemeDTO.getThresholdConfigDTOS())  || warnSchemeDTO.getThresholdConfigDTOS().isEmpty()) {
            throw new RuntimeException("阈值配置不能为空！");
        }
        if (ObjectUtil.isNull(warnSchemeDTO.getIncrementConfigDTOS())  || warnSchemeDTO.getIncrementConfigDTOS().isEmpty()) {
            throw new RuntimeException("增量配置不能为空！");
        }
        if (ObjectUtil.isNull(warnSchemeDTO.getGrowthRateConfigDTOS())  || warnSchemeDTO.getGrowthRateConfigDTOS().isEmpty()) {
            throw new RuntimeException("增速配置不能为空！");
        }
        Long selectCount = warnSchemeMapper.selectCount(new LambdaQueryWrapper<WarnSchemeEntity>()
                .eq(WarnSchemeEntity::getWarnSchemeName, warnSchemeDTO.getWarnSchemeName())
                .eq(WarnSchemeEntity::getWorkFaceId, warnSchemeDTO.getWorkFaceId())
                .eq(WarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (selectCount > 0) {
            throw new RuntimeException("同一个工作面下,该名称的方案已存在！");
        }
        List<ThresholdConfigDTO> thresholdConfigDTOS = warnSchemeDTO.getThresholdConfigDTOS();
        List<Map<String, Object>> thresholdMap = ConvertUtils.convertThresholdMap(thresholdConfigDTOS);

        List<IncrementConfigDTO> incrementConfigDTOS = warnSchemeDTO.getIncrementConfigDTOS();
        List<Map<String, Object>> incrementMap = ConvertUtils.convertIncrementMap(incrementConfigDTOS);

        List<GrowthRateConfigDTO> growthRateConfigDTOS = warnSchemeDTO.getGrowthRateConfigDTOS();
        List<Map<String, Object>> growthRateMap = ConvertUtils.convertGrowthRateMap(growthRateConfigDTOS);

        WarnSchemeEntity warnSchemeEntity = new WarnSchemeEntity();
        warnSchemeEntity.setWarnSchemeName(warnSchemeDTO.getWarnSchemeName());
        warnSchemeEntity.setSceneType(warnSchemeDTO.getSceneType());
        warnSchemeEntity.setWorkFaceId(warnSchemeDTO.getWorkFaceId());
        warnSchemeEntity.setQuietHour(warnSchemeDTO.getQuietHour());
        warnSchemeEntity.setThresholdConfig(thresholdMap);
        warnSchemeEntity.setIncrementConfig(incrementMap);
        warnSchemeEntity.setGrowthRateConfig(growthRateMap);
        warnSchemeEntity.setCreateTime(System.currentTimeMillis());
        warnSchemeEntity.setCreateBy(SecurityUtils.getUserId());
        warnSchemeEntity.setMineId(mineId);
        warnSchemeEntity.setStatus(warnSchemeDTO.getStatus());
        warnSchemeEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        flag = warnSchemeMapper.insert(warnSchemeEntity);
        if (flag <= 0) {
            throw new RuntimeException("添加失败,请联系管理员");
        }
        return flag;
    }

    @Override
    public int updateWarnScheme(WarnSchemeDTO warnSchemeDTO) {
        int flag = 0;
        if (ObjectUtil.isNull(warnSchemeDTO)) {
            throw new RuntimeException("参数错误,不能为空！");
        }
        if (ObjectUtil.isNull(warnSchemeDTO.getWarnSchemeId())) {
            throw new RuntimeException("参数错误!");
        }
        WarnSchemeEntity warnSchemeEntity = warnSchemeMapper.selectOne(new LambdaQueryWrapper<WarnSchemeEntity>()
                .eq(WarnSchemeEntity::getWarnSchemeId, warnSchemeDTO.getWarnSchemeId())
                .eq(WarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(warnSchemeEntity)) {
            throw new RuntimeException("未找到预警方案！!");
        }

        Long warnSchemeId = warnSchemeEntity.getWarnSchemeId();

        if (warnSchemeEntity.getStatus().equals(ConstantsInfo.SCHEME_ENABLE)) {
            throw new RuntimeException("该预警方案已启用,不能修改！");
        } else {
            Long selectCount = warnSchemeMapper.selectCount(new LambdaQueryWrapper<WarnSchemeEntity>()
                    .eq(WarnSchemeEntity::getWarnSchemeName, warnSchemeDTO.getWarnSchemeName())
                    .eq(WarnSchemeEntity::getWorkFaceId, warnSchemeDTO.getWorkFaceId())
                    .eq(WarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                    .ne(WarnSchemeEntity::getWarnSchemeId, warnSchemeDTO.getWarnSchemeId()));
            if (selectCount > 0) {
                throw new RuntimeException("同一个工作面下,该名称的方案已存在！");
            }
            if (ObjectUtil.isNull(warnSchemeDTO)) {
                throw new RuntimeException("参数错误,不能为空！");
            }
            if (ObjectUtil.isNull(warnSchemeDTO.getThresholdConfigDTOS())  || warnSchemeDTO.getThresholdConfigDTOS().isEmpty()) {
                throw new RuntimeException("阈值配置不能为空！");
            }
            if (ObjectUtil.isNull(warnSchemeDTO.getIncrementConfigDTOS())  || warnSchemeDTO.getIncrementConfigDTOS().isEmpty()) {
                throw new RuntimeException("增量配置不能为空！");
            }
            if (ObjectUtil.isNull(warnSchemeDTO.getGrowthRateConfigDTOS())  || warnSchemeDTO.getGrowthRateConfigDTOS().isEmpty()) {
                throw new RuntimeException("增速配置不能为空！");
            }

            List<ThresholdConfigDTO> thresholdConfigDTOS = warnSchemeDTO.getThresholdConfigDTOS();
            List<Map<String, Object>> thresholdMap = ConvertUtils.convertThresholdMap(thresholdConfigDTOS);

            List<IncrementConfigDTO> incrementConfigDTOS = warnSchemeDTO.getIncrementConfigDTOS();
            List<Map<String, Object>> incrementMap = ConvertUtils.convertIncrementMap(incrementConfigDTOS);

            List<GrowthRateConfigDTO> growthRateConfigDTOS = warnSchemeDTO.getGrowthRateConfigDTOS();
            List<Map<String, Object>> growthRateMap = ConvertUtils.convertGrowthRateMap(growthRateConfigDTOS);

            warnSchemeEntity.setWarnSchemeId(warnSchemeId);
            warnSchemeEntity.setWarnSchemeName(warnSchemeDTO.getWarnSchemeName());
            warnSchemeEntity.setSceneType(warnSchemeDTO.getSceneType());
            warnSchemeEntity.setWorkFaceId(warnSchemeDTO.getWorkFaceId());
            warnSchemeEntity.setQuietHour(warnSchemeDTO.getQuietHour());
            warnSchemeEntity.setThresholdConfig(thresholdMap);
            warnSchemeEntity.setIncrementConfig(incrementMap);
            warnSchemeEntity.setGrowthRateConfig(growthRateMap);
            warnSchemeEntity.setUpdateTime(System.currentTimeMillis());
            warnSchemeEntity.setUpdateBy(SecurityUtils.getUserId());
            flag = warnSchemeMapper.updateById(warnSchemeEntity);
            if (flag <= 0) {
                throw new RuntimeException("预警方案修改失败");
            }
        }
        return flag;
    }

    @Override
    public WarnSchemeDTO detail(Long warnSchemeId) {
        if (ObjectUtil.isNull(warnSchemeId)) {
            throw new RuntimeException("参数错误,请选择有效的数据ID!");
        }
        WarnSchemeEntity warnSchemeEntity = warnSchemeMapper.selectOne(new LambdaQueryWrapper<WarnSchemeEntity>()
                .eq(WarnSchemeEntity::getWarnSchemeId, warnSchemeId)
                .eq(WarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(warnSchemeEntity)) {
            throw new RuntimeException("未找到该预警方案！");
        }
        WarnSchemeDTO warnSchemeDTO = new WarnSchemeDTO();
        warnSchemeDTO.setWarnSchemeId(warnSchemeEntity.getWarnSchemeId());
        warnSchemeDTO.setWarnSchemeName(warnSchemeEntity.getWarnSchemeName());
        warnSchemeDTO.setSceneType(warnSchemeEntity.getSceneType());
        warnSchemeDTO.setWorkFaceId(warnSchemeEntity.getWorkFaceId());
        warnSchemeDTO.setQuietHour(warnSchemeEntity.getQuietHour());
        warnSchemeDTO.setStatus(warnSchemeEntity.getStatus());

        List<ThresholdConfigDTO> thresholdConfigDTOList = WholeSchemeUtils.getThresholdConfig(warnSchemeId, warnSchemeMapper);
        warnSchemeDTO.setThresholdConfigDTOS(thresholdConfigDTOList);

        List<IncrementConfigDTO> incrementConfigDTOList = WholeSchemeUtils.getIncrementConfig(warnSchemeId, warnSchemeMapper);
        warnSchemeDTO.setIncrementConfigDTOS(incrementConfigDTOList);

        List<GrowthRateConfigDTO> growthRateConfigDTOList = WholeSchemeUtils.getGrowthRateConfig(warnSchemeId, warnSchemeMapper);
        warnSchemeDTO.setGrowthRateConfigDTOS(growthRateConfigDTOList);

        return warnSchemeDTO;
    }

    @Override
    public TableData pageQueryList(WarnSchemeSelectDTO warnSchemeSelectDTO, Long mineId, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
//        LoginUser loginUser = SecurityUtils.getLoginUser();
//        String token = loginUser.getToken();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<WarnSchemeVO> page = warnSchemeMapper.queryPage(warnSchemeSelectDTO, mineId);
        Page<WarnSchemeVO> warnSchemeVOPage = getListFmt(page);
        result.setTotal(warnSchemeVOPage.getTotal());
        result.setRows(warnSchemeVOPage.getResult());
        return result;
    }

    @Override
    public int batchEnableDisable(Long[] warnSchemeIds) {
        int flag = 0;
        if (warnSchemeIds.length == 0) {
            throw new RuntimeException("请选择要禁用/启用的数据!");
        }
        List<Long> ids = Arrays.asList(warnSchemeIds);
        ids.forEach(warnSchemeId -> {
            WarnSchemeEntity warnSchemeEntity = warnSchemeMapper.selectOne(new LambdaQueryWrapper<WarnSchemeEntity>()
                    .eq(WarnSchemeEntity::getWarnSchemeId, warnSchemeId)
                    .eq(WarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(warnSchemeEntity)) {
                throw new RuntimeException("未找到id为" + warnSchemeId + "的预警方案数据");
            }
            if (StringUtils.equals(ConstantsInfo.SCHEME_DISABLE, warnSchemeEntity.getStatus())) {
                warnSchemeEntity.setStatus(ConstantsInfo.SCHEME_ENABLE);
            } else {
                warnSchemeEntity.setStatus(ConstantsInfo.SCHEME_DISABLE);
            }
            warnSchemeMapper.updateById(warnSchemeEntity);
        });
        return flag;
    }

    @Override
    public boolean deleteByIds(Long[] warnSchemeIds) {
        boolean flag = false;
        if (warnSchemeIds.length == 0) {
            throw new RuntimeException("请选择要删除的数据!");
        }
        List<Long> ids = Arrays.asList(warnSchemeIds);
        ids.forEach(warnSchemeId -> {
            WarnSchemeEntity warnSchemeEntity = warnSchemeMapper.selectOne(new LambdaQueryWrapper<WarnSchemeEntity>()
                    .eq(WarnSchemeEntity::getWarnSchemeId, warnSchemeId)
                    .eq(WarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(warnSchemeEntity)) {
                throw new RuntimeException("未找到id为" + warnSchemeId + "的预警方案数据");
            }
            if (StringUtils.equals(ConstantsInfo.SCHEME_ENABLE, warnSchemeEntity.getStatus())) {
                throw new RuntimeException("该方案已启用,不能删除");
            }
        });
        flag = this.removeBatchByIds(ids);
        return flag;
    }


    /**
     * VO数据格式化
     */
    private Page<WarnSchemeVO> getListFmt(Page<WarnSchemeVO> list) {
        if (ListUtils.isNotNull(list.getResult())) {
            list.getResult().forEach(warnSchemeVO -> {
                warnSchemeVO.setWorkFaceNameFmt(getWorkFaceName(warnSchemeVO.getWorkFaceId()));
                List<ThresholdConfigDTO> thresholdConfigDTOList = WholeSchemeUtils.getThresholdConfig(warnSchemeVO.getWarnSchemeId(), warnSchemeMapper);
                warnSchemeVO.setThresholdConfigDTOS(thresholdConfigDTOList);

                List<IncrementConfigDTO> incrementConfigDTOList = WholeSchemeUtils.getIncrementConfig(warnSchemeVO.getWarnSchemeId(), warnSchemeMapper);
                warnSchemeVO.setIncrementConfigDTOS(incrementConfigDTOList);

                List<GrowthRateConfigDTO> growthRateConfigDTOList = WholeSchemeUtils.getGrowthRateConfig(warnSchemeVO.getWarnSchemeId(), warnSchemeMapper);
                warnSchemeVO.setGrowthRateConfigDTOS(growthRateConfigDTOList);

                String duration = getDuration(incrementConfigDTOList);
                warnSchemeVO.setDuration(duration);
            });
        }
        return list;
    }

    private String getWorkFaceName(Long workFaceId) {
        BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                .eq(BizWorkface::getWorkfaceId, workFaceId)
                .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizWorkface)) {
            return "";
        }
        return bizWorkface.getWorkfaceName();
    }

    private String getDuration(List<IncrementConfigDTO> incrementConfigDTOList) {
        String duration = "";
        if (ListUtils.isNotNull(incrementConfigDTOList)) {
            duration = incrementConfigDTOList.get(0).getDuration();
        }
        return duration;
    }
}