package com.ruoyi.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.BizDrillRecord;
import com.ruoyi.system.domain.BizVideo;

/**
 * 工程视频Service接口
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
public interface IBizVideoService  extends IService<BizVideo>
{
    List<BizVideo> list();
    /**
     * 查询工程视频
     * 
     * @param videoId 工程视频主键
     * @return 工程视频
     */

    /**
     * 查询工程视频列表
     * 
     * @param bizVideo 工程视频
     * @return 工程视频集合
     */

    /**
     * 新增工程视频
     * 
     * @param bizVideo 工程视频
     * @return 结果
     */

    /**
     * 修改工程视频
     * 
     * @param bizVideo 工程视频
     * @return 结果
     */

    /**
     * 批量删除工程视频
     * 
     * @param videoIds 需要删除的工程视频主键集合
     * @return 结果
     */

    /**
     * 删除工程视频信息
     * 
     * @param videoId 工程视频主键
     * @return 结果
     */
}
