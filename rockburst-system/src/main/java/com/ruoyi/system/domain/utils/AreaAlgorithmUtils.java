package com.ruoyi.system.domain.utils;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.Entity.PlanAreaEntity;
import com.ruoyi.system.domain.dto.PlanAreaDTO;
import com.ruoyi.system.domain.dto.TraversePointGatherDTO;
import com.ruoyi.system.mapper.BizTravePointMapper;
import com.ruoyi.system.mapper.PlanAreaMapper;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: shikai
 * @date: 2025/2/20
 * @description:
 */
public class AreaAlgorithmUtils {

    /**
     * 自定义异常类
     */
    private static final class AreaAlgorithmException extends RuntimeException {
        public AreaAlgorithmException(String message) {
            super(message);
        }
    }

    public static void areaCheck(PlanAreaDTO planAreaDTO, PlanAreaEntity planAreaEntity,
                                 List<PlanAreaEntity> planAreaEntities, PlanAreaMapper planAreaMapper, BizTravePointMapper bizTravePointMapper) {
        // 判断输入起始导线点与对照体起始导线点是否相同
        validateStartDistance(planAreaDTO, planAreaEntity, bizTravePointMapper);
        // 判断输入起始导线点与对照体终始导线点是否相同
        validatePlanArea(planAreaDTO, planAreaEntity, planAreaEntities, bizTravePointMapper, planAreaMapper);
    }

    /**
     * 主算法逻辑一
     */
    private static void validateStartDistance(PlanAreaDTO planAreaDTO, PlanAreaEntity planAreaEntity, BizTravePointMapper bizTravePointMapper) {
        if (Objects.equals(planAreaDTO.getStartTraversePointId(), planAreaEntity.getStartTraversePointId())) {
            char firstChar = planAreaEntity.getStartDistance().charAt(0);
            char dtoFirstChar = planAreaDTO.getStartDistance().charAt(0);
            // 判断输入的起始距离与对照体的起始距离方向是否一致
            if (dtoFirstChar != firstChar) {
                throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域与之前计划区域有重叠,请重新输入");
            }
            // 判断是否 输入的起始距离 < 对照体的起始距离
            if (!DataJudgeUtils.compare(planAreaDTO.getStartDistance(), planAreaEntity.getStartDistance())) {
                throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域与之前计划区域有重叠,请重新输入");
            }
            validateTraversalPoints(planAreaDTO, planAreaEntity, firstChar, bizTravePointMapper);
        }
    }

    /**
     * 【输入的起始距离与对照体的起始距离一致 and 输入的起始距离 < 对照体的起始距离】算法逻辑
     */
    private static void validateTraversalPoints(PlanAreaDTO planAreaDTO, PlanAreaEntity planAreaEntity, char direction, BizTravePointMapper bizTravePointMapper) {
        BizTravePoint startBizTravePoint = getBizTravePoint(planAreaEntity.getStartTraversePointId(), bizTravePointMapper);
        Long startNo = Objects.requireNonNull(startBizTravePoint).getNo();
        BizTravePoint endTraPoint = getBizTravePoint(planAreaDTO.getEndTraversePointId(), bizTravePointMapper);
        BizTravePoint startTraPoint = getBizTravePoint(planAreaDTO.getStartTraversePointId(), bizTravePointMapper);
        // 判断对照体起始距离方向
        if (direction == '-') {
            // 判断是否 输入的终始点 < 对照体起始点下一个导线点 and 输入的起始点 < 输入的终始点
            if (Objects.requireNonNull(endTraPoint).getNo() < startNo + 1L && Objects.requireNonNull(startTraPoint).getNo() < endTraPoint.getNo()) {
                handleNegativeDirection(planAreaDTO, planAreaEntity);
            } else {
                throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域不符合[起始导线点小于终始导线点]的规则!!");
            }
        } else {
            // 判断是否 输入的终始点 <= 对照体起始点下一个导线点 and 输入的起始点 < 输入的终始点
            if (Objects.requireNonNull(endTraPoint).getNo() <= startNo + 1L && Objects.requireNonNull(startTraPoint).getNo() < endTraPoint.getNo()) {
                handlePositiveDirection(planAreaDTO, planAreaEntity);
            } else {
                throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域不符合[起始导线点小于终始导线点]的规则!!");
            }
        }
    }

    /**
     * 【输入的终始点 < 对照体起始点下一个导线点 and 输入的起始点 < 输入的终始点】算法逻辑
     */
    private static void handleNegativeDirection(PlanAreaDTO planAreaDTO, PlanAreaEntity planAreaEntity) {
        // 判断输入终始点是否与对照体的起始点相同
        if (Objects.equals(planAreaDTO.getEndTraversePointId(), planAreaEntity.getStartTraversePointId())) {
            // 判断输入的终始距离方向是否与起始距离方向一致
            if (planAreaDTO.getEndDistance().charAt(0) == '-') {
                boolean compare = DataJudgeUtils.compare(planAreaDTO.getStartDistance(), planAreaDTO.getEndDistance());
                boolean compareTwo = DataJudgeUtils.compareTwo(planAreaDTO.getEndDistance(), planAreaEntity.getStartDistance());
                // 判断是否 输入的终始距离 > 输入起始距离 and 输入的终始距离 <= 对照体的起始距离
                if (!compare && compareTwo) {
                    throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域与之前计划区域有重叠,请重新输入");
                }
            } else {
                throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域与之前计划区域有重叠,请重新输入");
            }
        }
    }

    /**
     * 【输入的终始点 <= 对照体起始点下一个导线点 and 输入的起始点 < 输入的终始点】算法逻辑
     */
    private static void handlePositiveDirection(PlanAreaDTO planAreaDTO, PlanAreaEntity planAreaEntity) {
        // 判断输入终始点是否与对照体的起始点相同
        if (Objects.equals(planAreaDTO.getEndTraversePointId(), planAreaEntity.getStartTraversePointId())) {
            if (planAreaDTO.getEndDistance().charAt(0) == '-') {
                boolean compare = DataJudgeUtils.compare(planAreaDTO.getStartDistance(), planAreaDTO.getEndDistance());
                boolean compareTwo = DataJudgeUtils.compareTwo(planAreaDTO.getEndDistance(), planAreaEntity.getStartDistance());
                // 判断是否 输入的终始距离 > 输入起始距离 and 输入的终始距离 <= 对照体的起始距离
                if (!compare && compareTwo) {
                    throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域与之前计划区域有重叠,请重新输入");
                }
            } else {
                throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域不符合[起始导线点小于终始导线点]的规则!!");
            }
        }
    }

    /**
     * 主算法逻辑二
     */
    private static void validatePlanArea(PlanAreaDTO planAreaDTO, PlanAreaEntity planAreaEntity, List<PlanAreaEntity> planAreaEntities,
                                         BizTravePointMapper bizTravePointMapper, PlanAreaMapper planAreaMapper) {
        if (Objects.equals(planAreaDTO.getStartTraversePointId(), planAreaEntity.getEndTraversePointId())) {
            char firstChar = planAreaEntity.getEndDistance().charAt(0);
            char charAt = planAreaDTO.getStartDistance().charAt(0);
            // 判断输入的起始距离与对照体的终始距离方向是否一致
            if (charAt == firstChar) {
                boolean compare = DataJudgeUtils.compare(planAreaDTO.getStartDistance(), planAreaEntity.getEndDistance());
                // 判断输入的起始距离 > 对照体终始距离
                if (!compare) {
                    throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域与之前计划区域有重叠,请重新输入");
                }
                validateTraversalPointsTwo(planAreaDTO, planAreaEntity, planAreaEntities, bizTravePointMapper, planAreaMapper);
            } else {
                throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域与之前计划区域有重叠,请重新输入");
            }
        }
    }

    /**
     * 【输入的起始距离 > 对照体终始距离】算法逻辑
     */
    private static void validateTraversalPointsTwo(PlanAreaDTO planAreaDTO, PlanAreaEntity planAreaEntity, List<PlanAreaEntity> planAreaEntities,
                                            BizTravePointMapper bizTravePointMapper, PlanAreaMapper planAreaMapper) {
        Long eNo = getBizTravePoint(planAreaDTO.getEndTraversePointId(), bizTravePointMapper).getNo();
        Long sNo = getBizTravePoint(planAreaDTO.getStartTraversePointId(), bizTravePointMapper).getNo();
        // 判断 输入的终始点 >= 输入的起始点
        if (eNo < sNo) {
            throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域不符合[终始点大于等于起始点]的规则!!");
        }
        // 判断 输入的终始点 = 输入的起始点
        if (eNo.equals(sNo)) {
            validateSameTraversalPoints(planAreaDTO);
        } else {
            validateDifferentTraversalPoints(planAreaDTO, planAreaEntity, planAreaEntities, bizTravePointMapper, planAreaMapper, eNo);
        }
    }

    /**
     * 【输入的终始点 = 输入的起始点】算法逻辑
     */
    private static void validateSameTraversalPoints(PlanAreaDTO planAreaDTO) {
        boolean b = DataJudgeUtils.compare(planAreaDTO.getStartDistance(), planAreaDTO.getEndDistance());
        // 判断 输入的起始距离 < 输入的终始距离
        if (!b) {
            throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域不符合[终始点大于等于起始点]的规则!!");
        }
    }

    /**
     * 【输入的终始点 > 输入的起始点】算法逻辑
     * 【获取距离输入终始距离之后距离最近的计划】---for循环
     */
    private static void validateDifferentTraversalPoints(PlanAreaDTO planAreaDTO, PlanAreaEntity planAreaEntity, List<PlanAreaEntity> planAreaEntities,
                                                  BizTravePointMapper bizTravePointMapper, PlanAreaMapper planAreaMapper,  Long eNo) {
        Long initialNo = getBizTravePoint(planAreaEntity.getEndTraversePointId(), bizTravePointMapper).getNo();
        Integer maxNo = bizTravePointMapper.selectMaxNo(planAreaDTO.getTunnelId());
        AtomicReference<Long> planAreaId = new AtomicReference<>(0L);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            int startNo = Math.toIntExact(initialNo) + 1;
            for (int no = startNo; no <= maxNo; no++) {
                for (PlanAreaEntity pae : planAreaEntities) {
                    if (no == pae.getStartTraversePointId()) {
                        if (no == eNo) {
                            planAreaId.set(pae.getPlanId());
                        }
                    } else {
                        handleTraversePointGather(pae, planAreaDTO, planAreaId, bizTravePointMapper, objectMapper, eNo);
                    }
                }
            }
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("initialNo 超出 int 范围", e);
        }

        PlanAreaEntity planArea = planAreaMapper.selectOne(new LambdaQueryWrapper<PlanAreaEntity>()
                .eq(PlanAreaEntity::getPlanAreaId, planAreaId.get()));
        validateConditionDistance(planAreaDTO, planArea, bizTravePointMapper);
    }

    /**
     * 【输入终始导线点与获取的条件起始条件不同之后】算法逻辑
     */
    private static void handleTraversePointGather(PlanAreaEntity pae, PlanAreaDTO planAreaDTO, AtomicReference<Long> planAreaId,
                                           BizTravePointMapper bizTravePointMapper, ObjectMapper objectMapper, long eNo) {
        String traversePointGather = pae.getTraversePointGather();
        if (traversePointGather != null && !traversePointGather.isEmpty()) {
            try {
                List<TraversePointGatherDTO> tPointGatherDTOS = objectMapper.readValue(traversePointGather, new TypeReference<List<TraversePointGatherDTO>>() {});
                if (tPointGatherDTOS != null && !tPointGatherDTOS.isEmpty()) {
                    for (TraversePointGatherDTO tpg : tPointGatherDTOS) {
                        Long targetNo = getBizTravePoint(tpg.getTraversePointId(), bizTravePointMapper).getNo();
                        if (targetNo.intValue() == eNo) {
                            if (planAreaDTO.getEndTraversePointId().equals(pae.getStartTraversePointId())) {
                                planAreaId.set(pae.getPlanId());
                            } else {
                                throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域与之前计划区域有重叠,请重新输入");
                            }
                        }
                    }
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON 解析失败: " + e.getMessage(), e);
            }
        } else {
            BizTravePoint bizTravePoint = getBizTraPointTwo(pae.getTunnelId(), (long) pae.getStartTraversePointId(), bizTravePointMapper);
            if (pae.getStartTraversePointId().equals(bizTravePoint.getPointId())) {
                planAreaId.set(pae.getPlanId());
            }
        }
    }

    /**
     * 【拿到符合条件的计划区域之后的】算法逻辑
     */
    private static void validateConditionDistance(PlanAreaDTO planAreaDTO, PlanAreaEntity planArea, BizTravePointMapper bizTravePointMapper) {
        String conditionDistance = planArea.getStartDistance();
        char symbol = conditionDistance.charAt(0);
        // 判断标志点起始距离方向是否为'-'
        if (symbol == '-') {
            validateNegativeSymbol(planAreaDTO, planArea, symbol, bizTravePointMapper);
        } else {
            validatePositiveSymbol(planAreaDTO, planArea, symbol, bizTravePointMapper);
        }
    }

    /**
     * 【标志点起始距离方向为'-'】算法逻辑
     */
    private static void validateNegativeSymbol(PlanAreaDTO planAreaDTO, PlanAreaEntity planArea, char symbol, BizTravePointMapper bizTravePointMapper) {
        // 判断输入的终始导线点 = 标志点起始导线点
        if (planAreaDTO.getEndTraversePointId().equals(planArea.getStartTraversePointId())) {
            // 判断输入的终始距离与标记点起始距离方向是否相同
            if (planAreaDTO.getEndDistance().charAt(0) == symbol) {
                boolean compared = DataJudgeUtils.compareTwo(planAreaDTO.getEndDistance(), planArea.getStartDistance());
                // 判断输入的终始距离 <= 标记点起始距离
                if (!compared) {
                    throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域不符合[终始区域大于起始区域的规则]!!");
                }
            } else {
                throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域不符合[终始区域大于起始区域的规则]!!");
            }
        } else {
            if (planAreaDTO.getEndDistance().charAt(0) == '-') {
                // 获取输入终始导线点的距前一个导线点的距离
                Double prePointDistance = getPrePointDistance(planAreaDTO.getEndTraversePointId(), bizTravePointMapper);
                // 获取距离差
                double v = DataJudgeUtils.doingPoorly(prePointDistance, Double.valueOf(planAreaDTO.getStartDistance()));
                boolean avc = DataJudgeUtils.absoluteValueCompareTwo(v, planAreaDTO.getEndDistance());
                // 判断输入的终始距离是否小于距离差
                if (!avc) {
                    throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域不符合[终始区域大于起始区域的规则]!!");
                }
            } else {
                // 获取标志起始导线点与上一个导线点的距离
                Double prePointDistance = getPrePointDistance(planArea.getStartTraversePointId(), bizTravePointMapper);
                // 获取去除标志起始距离的距离
                double a = DataJudgeUtils.doingPoorly(prePointDistance, Double.valueOf(planArea.getStartDistance()));
                boolean b = DataJudgeUtils.absoluteValueCompare(String.valueOf(a), planAreaDTO.getEndDistance());
                if (!b) {
                    throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域与之前计划区域有重叠,请重新输入");
                }
            }
        }
    }

    /**
     * 【标志点起始距离方向为'+'】算法逻辑
     */
    private static void validatePositiveSymbol(PlanAreaDTO planAreaDTO, PlanAreaEntity planArea, char symbol, BizTravePointMapper bizTravePointMapper) {
        // 判断输入的终始导线点 = 标志点起始导线点
        if (planAreaDTO.getEndTraversePointId().equals(planArea.getStartTraversePointId())) {
            // 判断输入的终始距离与标记点起始距离方向是否相同
            if (planAreaDTO.getEndDistance().charAt(0) == symbol) {
                boolean compared = DataJudgeUtils.compareTwo(planAreaDTO.getEndDistance(), planArea.getStartDistance());
                // 判断输入的终始距离 <= 标记点起始距离
                if (!compared) {
                    throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域不符合[终始区域大于起始区域的规则]!!");
                }
            } else {
                // 获取输入终始导线点的距前一个导线点的距离
                Double prePointDistance = getPrePointDistance(planAreaDTO.getEndTraversePointId(), bizTravePointMapper);
                // 获取距离差
                double v = DataJudgeUtils.doingPoorly(prePointDistance, Double.valueOf(planAreaDTO.getStartDistance()));
                // 判断输入的终始距离是否小于距离差
                boolean avc = DataJudgeUtils.absoluteValueCompareTwo(v, planAreaDTO.getEndDistance());
                if (!avc) {
                    throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域不符合[终始区域大于起始区域的规则]!!");
                }
            }
        } else {
            if (planAreaDTO.getEndDistance().charAt(0) == '-') {
                BizTravePoint travePoint = getBizTravePoint(planArea.getStartTraversePointId(), bizTravePointMapper);
                // 获取标记点开始点下一个导线点
                BizTravePoint bizTravePoint = getBizTraPointTwo(planArea.getTunnelId(), travePoint.getNo(), bizTravePointMapper);
                // 获取两个导线点距离
                Double distance = getPrePointDistance(bizTravePoint.getPointId(), bizTravePointMapper);
                // 获取总距离-标记点开始点距离的差值
                double doingPoorly = DataJudgeUtils.doingPoorly(distance, Double.valueOf(planArea.getStartDistance()));
                String s = "-" + doingPoorly;
                boolean compared = DataJudgeUtils.compare(planAreaDTO.getEndDistance(), s);
                // 判断输入的终始距离是否小于标记点起始距离的反转距离
                if (!compared) {
                    throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域与之前计划区域有重叠,请重新输入");
                }
            } else {
                throw new AreaAlgorithmUtils.AreaAlgorithmException("输入的区域与之前计划区域有重叠,请重新输入");
            }
        }
    }

    /**
     * 获取距前一个导线点的距离
     */
    private static Double getPrePointDistance(Long pointId, BizTravePointMapper bizTravePointMapper) {
        BizTravePoint bizTravePoint = bizTravePointMapper.selectOne(new LambdaQueryWrapper<BizTravePoint>()
                .eq(BizTravePoint::getPointId, pointId)
                .eq(BizTravePoint::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizTravePoint)) {
            throw new RuntimeException("获取导线点异常(1)！");
        }
        return bizTravePoint.getPrePointDistance();
    }

    /**
     * 获取导线点
     */
    private static BizTravePoint getBizTravePoint(Long pointId, BizTravePointMapper bizTravePointMapper) {
        BizTravePoint bizTravePoint = bizTravePointMapper.selectOne(new LambdaQueryWrapper<BizTravePoint>()
                .eq(BizTravePoint::getPointId, pointId)
                .eq(BizTravePoint::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizTravePoint)) {
            throw new RuntimeException("发生未知异常，请联系管理员!");
        }
        return bizTravePoint;
    }

    /**
     * 获取导线点二
     */
    private static BizTravePoint getBizTraPointTwo(Long tunnelId, Long no, BizTravePointMapper bizTravePointMapper) {
        BizTravePoint bizTravePoint = new BizTravePoint();
        bizTravePoint = bizTravePointMapper.selectOne(new LambdaQueryWrapper<BizTravePoint>()
                .eq(BizTravePoint::getTunnelId, tunnelId)
                .eq(BizTravePoint::getNo, no + 1)
                .eq(BizTravePoint::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizTravePoint)) {
            throw new RuntimeException("获取导线点异常(2)！");
        }
        return bizTravePoint;
    }
}