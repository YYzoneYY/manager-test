package com.ruoyi.system.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.BizDrillRecord;
import com.ruoyi.system.domain.BizVideo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工程视频Mapper接口
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Mapper
public interface BizVideoMapper extends BaseMapper<BizVideo>
{
    /**
     * 查询工程视频
     * 
     * @param videoId 工程视频主键
     * @return 工程视频
     */
//    public BizVideo selectBizVideoByVideoId(Long videoId);

    /**
     * 查询工程视频列表
     * 
     * @param bizVideo 工程视频
     * @return 工程视频集合
     */
//    public List<BizVideo> selectBizVideoList(BizVideo bizVideo);

    /**
     * 新增工程视频
     * 
     * @param bizVideo 工程视频
     * @return 结果
     */
//    public int insertBizVideo(BizVideo bizVideo);

    /**
     * 修改工程视频
     * 
     * @param bizVideo 工程视频
     * @return 结果
     */
//    public int updateBizVideo(BizVideo bizVideo);

    /**
     * 删除工程视频
     * 
     * @param videoId 工程视频主键
     * @return 结果
     */
//    public int deleteBizVideoByVideoId(Long videoId);

    /**
     * 批量删除工程视频
     * 
     * @param videoIds 需要删除的数据主键集合
     * @return 结果
     */
//    public int deleteBizVideoByVideoIds(Long[] videoIds);
}
