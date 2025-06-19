package com.ruoyi.quartz.task;

/**
 * 定时任务调度测试
 * 
 * @author ruoyi
 */
//@Component("ryTask")
public interface IRyTask
{
    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i);

    public void ryParams(String params);

    public void ryNoParams();

    /**
     * 工程进度预警
     */
    public void alarmProject();

    /**
     * 预警某段时间间隔后重新推送
     */
    public void alarmAgainPush();

    /**
     * 视频识别
     */
    public void ai_model();
}
