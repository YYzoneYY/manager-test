package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.dto.PlanDTO;
import com.ruoyi.system.domain.dto.ProjectWarnChoiceListDTO;
import com.ruoyi.system.domain.dto.ReturnDTO;
import com.ruoyi.system.domain.dto.SelectNewPlanDTO;
import com.ruoyi.system.domain.vo.PlanVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/22
 * @description:
 */
public interface PlanService extends IService<PlanEntity> {

    /**
     * 工程计划新增
     * @param planDTO 参数DTO
     * @return 返回结果
     */
    int insertPlan(PlanDTO planDTO);

    /**
     * 工程计划修改
     * @param planDTO 参数DTO
     * @return 返回结果
     */
    int updatePlan(PlanDTO planDTO);

    /**
     * 根据id查询
     * @param planId 计划id
     * @return 返回结果
     */
    PlanDTO queryById(Long planId);

    /**
     * 分页查询
     * @param permission 权限
     * @param selectNewPlanDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    TableData queryPage(BasePermission permission, SelectNewPlanDTO selectNewPlanDTO, Integer pageNum, Integer pageSize);

    /**
     * 撤回
     * @param planId 计划id
     * @return 返回结果
     */
    String withdraw(Long planId);

    /**
     * 批量删除
     * @param planIds 主键id数组
     * @return 返回结果
     */
    boolean deletePlan(Long[] planIds);

    /**
     * 获取工程预警方案下拉列表
     * @return 返回结果
     */
    List<ProjectWarnChoiceListDTO> getProjectWarnChoiceList();

    /**
     * 导入
     * @param tag 标签 tag :1 掘进 2:回采
     * @param file 文件
     * @return 返回结果
     * @throws Exception 异常
     */
    String importPlan(String tag, MultipartFile file) throws Exception;

    /**
     * 根据巷道id和类型获取区域集合
     * @param tunnelIds 巷道ids
     * @param type 类型
     * @return 返回结果
     */
    List<ReturnDTO> getSketchMap(List<Long> tunnelIds, String type);

    /**
     * 获取模板文件URL
     * @param fileId 文件id
     * @return 返回结果
     */
    String getFileUrl(String fileId, String type);

}