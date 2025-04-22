package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizProjectAudit;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.mapper.BizProjectAuditMapper;
import com.ruoyi.system.mapper.BizProjectRecordMapper;
import com.ruoyi.system.service.IBizProjectAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.OptionalInt;

/**
 * 工程填报审核记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Service
public class BizProjectAuditServiceImpl  extends ServiceImpl<BizProjectAuditMapper, BizProjectAudit> implements IBizProjectAuditService
{
    @Autowired
    private BizProjectAuditMapper bizProjectAuditMapper;

    @Autowired
    private BizProjectRecordMapper bizProjectRecordMapper;

    @Override
    public int audit(Long projectId) {
        BizProjectRecord record = bizProjectRecordMapper.selectById(projectId);
        BizProjectRecord vo = new BizProjectRecord();
        if(record != null && record.getStatus() == BizBaseConstant.FILL_STATUS_TEAM_LOAD){
            vo.setProjectId(projectId).setStatus(BizBaseConstant.FILL_STATUS_TEAM_DOING);
        }
        if(record != null && record.getStatus() == BizBaseConstant.FILL_STATUS_TEAM_OK){
            vo.setProjectId(projectId).setStatus(BizBaseConstant.FILL_STATUS_DEART_DOING);
        }
        bizProjectRecordMapper.updateById(vo);
        return 1;
    }


    @Override
    public int addAudittEAM(BizProjectAudit teamAuditDTO) {
        QueryWrapper<BizProjectAudit> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(BizProjectAudit::getNo).eq(BizProjectAudit::getProjectId,teamAuditDTO.getProjectId());
        List<BizProjectAudit> list = bizProjectAuditMapper.selectList(queryWrapper);
        if(list != null && list.size() > 0){
            OptionalInt maxNo = list.stream()
                    .mapToInt(BizProjectAudit::getNo)
                    .max();
            if (maxNo.isPresent()) {
                teamAuditDTO.setNo(maxNo.getAsInt()+1);
            }
        }else {
            teamAuditDTO.setNo(1);
        }
        teamAuditDTO.setTag("team");
        bizProjectAuditMapper.insert(teamAuditDTO);
        BizProjectRecord bizProjectRecord = new BizProjectRecord();
        bizProjectRecord.setProjectId(teamAuditDTO.getProjectId())
                .setStatus(getTeamAuditStatus(teamAuditDTO.getStatus()));
        bizProjectRecordMapper.updateById(bizProjectRecord);
        return 0;
    }



    @Override
    public int addAuditDeart(BizProjectAudit teamAuditDTO) {

        QueryWrapper<BizProjectAudit> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(BizProjectAudit::getNo).eq(BizProjectAudit::getProjectId,teamAuditDTO.getProjectId());
        List<BizProjectAudit> list = bizProjectAuditMapper.selectList(queryWrapper);
        if(list != null && list.size() > 0){
            OptionalInt maxNo = list.stream()
                    .mapToInt(BizProjectAudit::getNo)
                    .max();
            if (maxNo.isPresent()) {
                teamAuditDTO.setNo(maxNo.getAsInt()+1);
            }
        }else {
            return 0;
        }
        teamAuditDTO.setTag("deart");
        bizProjectAuditMapper.insert(teamAuditDTO);
        BizProjectRecord bizProjectRecord = new BizProjectRecord();
        bizProjectRecord.setProjectId(teamAuditDTO.getProjectId())
                .setStatus(getDeartAuditStatus(teamAuditDTO.getStatus()));
        bizProjectRecordMapper.updateById(bizProjectRecord);
        return 1;
    }


    int getDeartAuditStatus(Integer status){
        if(status == 1){
            return BizBaseConstant.FILL_STATUS_DEART_OK;
        }
        if(status == 0){
            return BizBaseConstant.FILL_STATUS_DEART_BACK;
        }
        return BizBaseConstant.FILL_STATUS_DEART_OK;
    }

    int getTeamAuditStatus(Integer status){
        if(status == 1){
            return BizBaseConstant.FILL_STATUS_TEAM_OK;
        }
        if(status == 0){
            return BizBaseConstant.FILL_STATUS_TEAM_BACK;
        }
        return BizBaseConstant.FILL_STATUS_TEAM_OK;
    }
}
