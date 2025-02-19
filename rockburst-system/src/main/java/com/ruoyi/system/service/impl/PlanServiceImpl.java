package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.dto.PlanDTO;
import com.ruoyi.system.domain.utils.DataJudgeUtils;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.ContentsService;
import com.ruoyi.system.service.PlanAreaService;
import com.ruoyi.system.service.PlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author: shikai
 * @date: 2024/11/22
 * @description:
 */

@Service
@Transactional
public class PlanServiceImpl extends ServiceImpl<PlanMapper, PlanEntity> implements PlanService {

    @Resource
    private PlanMapper planMapper;

    private PlanAreaMapper planAreaMapper;

    @Resource
    private PlanAuditMapper planAuditMapper;

    @Resource
    private SysDictDataMapper sysDictDataMapper;

    @Resource
    private BizProjectRecordMapper bizProjectRecordMapper;

    @Resource
    private ProjectWarnSchemeMapper projectWarnSchemeMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private BizTravePointMapper bizTravePointMapper;

    @Override
    public int insertPlan(PlanDTO planDTO) {
        int flag = 0;
        return 0;
    }

    @Override
    public int updatePlan(PlanDTO planDTO) {
        return 0;
    }

    private void checkParameter(PlanDTO planDTO) {
        if (ObjectUtil.isNull(planDTO)) {
            throw new RuntimeException("参数错误,参数不能为空");
        }
        if (ListUtils.isNull(planDTO.getPlanAreaDTOS())) {
            throw new RuntimeException("区域信息不能为空");
        }
    }

    private void checkArea(PlanDTO planDTO) {
        if (ListUtils.isNull(planDTO.getPlanAreaDTOS()) || planDTO.getPlanAreaDTOS().isEmpty()) {
            throw new RuntimeException("区域信息不能为空");
        }
        planDTO.getPlanAreaDTOS().forEach(planAreaDTO -> {
            List<PlanAreaEntity> planAreaEntities = planAreaMapper.selectList(new LambdaQueryWrapper<PlanAreaEntity>()
                    .eq(PlanAreaEntity::getTunnelId, planAreaDTO.getTunnelId()));
            if (ListUtils.isNotNull(planAreaEntities)) {
                planAreaEntities.forEach(planAreaEntity -> {
                    // 判断起始导线点是否相同
                    if (Objects.equals(planAreaDTO.getStartTraversePointId(), planAreaEntity.getStartTraversePointId())) {
                        char firstChar = planAreaEntity.getStartDistance().charAt(0);
                        char charAt = planAreaDTO.getStartDistance().charAt(0);
                        // 判断输入的起始距离与对照体的起始距离方向是否一致
                        if (charAt != firstChar) {
                            throw new RuntimeException("输入的区域与之前计划区域有重叠,请重新输入");
                        } else {
                            boolean b = DataJudgeUtils.compare(planAreaDTO.getStartDistance(), planAreaEntity.getStartDistance());
                            // 判断是否 输入的起始距离 < 对照体的起始距离
                            if (b) {
                                BizTravePoint bizTravePoint = getBizTravePoint(planAreaEntity.getStartTraversePointId());
                                Long no = bizTravePoint.getNo();
                                BizTravePoint eTraPoint = getBizTravePoint(planAreaDTO.getEndTraversePointId());
                                BizTravePoint sTraPoint = getBizTravePoint(planAreaDTO.getStartTraversePointId());
                                // 判断对照体起始距离方向
                                if (firstChar == '-') {
                                    // 判断是否 输入的终始点 < 对照体起始点下一个导线点 and 输入的起始点 < 输入的终始点
                                    if (eTraPoint.getNo() < no + 1L && sTraPoint.getNo() < eTraPoint.getNo()) {
                                        // 判断输入终始点是否与对照体的起始点相同
                                        if (Objects.equals(planAreaDTO.getEndTraversePointId(), planAreaEntity.getStartTraversePointId())) {
                                            // 判断输入的终始距离方向是否与起始距离方向一致
                                            if (planAreaDTO.getEndDistance().charAt(0) == '-') {
                                                boolean compare = DataJudgeUtils.compare(planAreaDTO.getStartDistance(), planAreaDTO.getEndDistance());
                                                boolean compareTwo = DataJudgeUtils.compareTwo(planAreaDTO.getEndDistance(), planAreaEntity.getStartDistance());
                                                // 判断是否 输入的终始距离 > 输入起始距离 and 输入的终始距离 <= 对照体的起始距离
                                                if (!compare && compareTwo) {
                                                    throw new RuntimeException("输入的区域与之前计划区域有重叠,请重新输入");
                                                }
                                            } else {
                                                throw new RuntimeException("输入的区域与之前计划区域有重叠,请重新输入");
                                            }
                                        }
                                    } else {
                                        throw new RuntimeException("输入的区域不符合[起始导线点小于终始导线点]的规则!!");
                                    }
                                } else {
                                    // 判断是否 输入的终始点 <= 对照体起始点下一个导线点 and 输入的起始点 < 输入的终始点
                                    if (eTraPoint.getNo() <= no + 1L && sTraPoint.getNo() < eTraPoint.getNo()) {
                                        // 判断输入终始点是否与对照体的起始点相同
                                        if (Objects.equals(planAreaDTO.getEndTraversePointId(), planAreaEntity.getStartTraversePointId())) {
                                            // 判断输入的终始距离方向是否与起始距离方向一致
                                            if (planAreaDTO.getEndDistance().charAt(0) == '-') {
                                                boolean compare = DataJudgeUtils.compare(planAreaDTO.getStartDistance(), planAreaDTO.getEndDistance());
                                                boolean compareTwo = DataJudgeUtils.compareTwo(planAreaDTO.getEndDistance(), planAreaEntity.getStartDistance());
                                                // 判断是否 输入的终始距离 > 输入起始距离 and 输入的终始距离 <= 对照体的起始距离
                                                if (!compare && compareTwo) {
                                                    throw new RuntimeException("输入的区域与之前计划区域有重叠,请重新输入");
                                                }
                                            } else {
                                                throw new RuntimeException("输入的区域不符合[起始导线点小于终始导线点]的规则!!");
                                            }
                                        }
                                    } else {
                                        throw new RuntimeException("输入的区域不符合[起始导线点小于终始导线点]的规则!!");
                                    }
                                }
                            } else {
                                throw new RuntimeException("输入的区域与之前计划区域有重叠,请重新输入");
                            }
                        }
                    }
                });

            }
        });

    }

    private BizTravePoint getBizTravePoint(Long pointId) {
        BizTravePoint bizTravePoint = bizTravePointMapper.selectOne(new LambdaQueryWrapper<BizTravePoint>()
                .eq(BizTravePoint::getPointId, pointId)
                .eq(BizTravePoint::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizTravePoint)) {
            throw new RuntimeException("发生未知异常，请联系管理员!");
        }
        return bizTravePoint;
    }
}