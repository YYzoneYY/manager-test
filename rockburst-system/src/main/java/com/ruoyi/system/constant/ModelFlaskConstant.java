package com.ruoyi.system.constant;


/**
 * ai视频识别接口名称
 * @author wdl
 * @date 2025-04-26
 */
public class ModelFlaskConstant {

    /**
     * video访问路径
     */
    public static final String static_video_url = "video/";

    /**
     * minio仓库
     */
    public static final String bucket_name = "bust-dev";

    /**
     * 参数配置前缀
     */
    public static final String pre_url = "video.model.url";

    /**
     * 上传视频接口
     */
    public static final String process_video = "process_video";
    /**
     * 根据任务ID查看任务状态
     */
    public static final String task_status = "task_status";
    /**
     * 下载
     */
    public static final String download = "download";
    /**
     * 查看任务列表
     */
    public static final String task_status_list = "task_status_list";


    /**
     * 任务redis键
     */
    public static final String ai_model_folder = "ai_model_tasks:";

    /**
     * 等待中
     */
    public static final String ai_model_pending = "pending";
    /**
     * 失败
     */
    public static final String ai_model_error = "error";
    /**
     * 识别中
     */
    public static final String ai_model_processing = "processing";
    /**
     * 完成
     */
    public static final String ai_model_done = "done";

}
