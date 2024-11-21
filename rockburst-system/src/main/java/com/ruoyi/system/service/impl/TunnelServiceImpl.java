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
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.SelectTunnelDTO;
import com.ruoyi.system.domain.dto.TunnelDTO;
import com.ruoyi.system.domain.vo.TunnelVO;
import com.ruoyi.system.mapper.BizWorkfaceMapper;
import com.ruoyi.system.mapper.SysDictDataMapper;
import com.ruoyi.system.mapper.TunnelMapper;
import com.ruoyi.system.service.TunnelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (selectCount > 0) {
            throw new RuntimeException("巷道名称已存在");
        }
        tunnelDTO.setCreateTime(System.currentTimeMillis());
        tunnelDTO.setCreateBy(1L);
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
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                .ne(TunnelEntity::getTunnelId, tunnelDTO.getTunnelId()));
        if (selectCount > 0) {
            throw new RuntimeException("巷道名称已存在");
        }
        tunnelDTO.setUpdateTime(System.currentTimeMillis());
        tunnelDTO.setUpdateBy(1L);
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
        TunnelEntity tunnelEntity = tunnelMapper.selectById(tunnelId);
        if (ObjectUtil.isNull(tunnelEntity)) {
            throw new RuntimeException("未找到此巷道");
        }
        TunnelDTO tunnelDTO = new TunnelDTO();
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
        return false;
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