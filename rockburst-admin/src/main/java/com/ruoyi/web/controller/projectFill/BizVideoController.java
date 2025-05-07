package com.ruoyi.web.controller.projectFill;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.quartz.task.IRyTask;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.constant.ModelFlaskConstant;
import com.ruoyi.system.domain.BizVideo;
import com.ruoyi.system.domain.dto.BizVideoDto;
import com.ruoyi.system.service.IBizVideoService;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.UploadService;
import com.ruoyi.system.service.impl.handle.AiModelHandle;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 工程视频Controller
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Api(tags = "project-工程视频")
@RestController
@RequestMapping("/project/video")
public class BizVideoController extends BaseController
{
    @Autowired
    private IBizVideoService bizVideoService;


    @Autowired
    private UploadService uploadService;

    @Autowired
    private ISysConfigService configService;

    @Resource
    private RestTemplate restTemplate;

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private AiModelHandle aiModelHandle;

    @Autowired
    private IRyTask iRyTask;

    /**
     * 查询工程视频列表
     */
    @ApiOperation("查询工程视频列表")
    @PreAuthorize("@ss.hasPermi('project:video:list')")
    @GetMapping("/list")
    public R<List<BizVideo>> list(@ParameterObject BizVideoDto dto)
    {
        QueryWrapper<BizVideo> queryWrapper = new QueryWrapper<BizVideo>();
        queryWrapper.lambda().like(StrUtil.isNotBlank(dto.getFileName()), BizVideo::getFileName, dto.getFileName());
        List<BizVideo> list = bizVideoService.list(queryWrapper);
        return R.ok(list);
    }



    /**
     * 获取工程视频详细信息
     */
    @ApiOperation("获取工程视频详细信息")
    @PreAuthorize("@ss.hasPermi('project:video:query')")
    @GetMapping(value = "/{videoId}")
    public R getInfo(@PathVariable("videoId") Long videoId)
    {
        return R.ok(bizVideoService.getById(videoId));
    }




    /**
     * 新增工程视频
     */
    @ApiOperation("新增工程视频")
    @PreAuthorize("@ss.hasPermi('project:video:add')")
    @Log(title = "工程视频", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BizVideoDto dto) {
        Assert.isTrue(dto.getProjectId() != null, "未绑定工程填报id");
        BizVideo entity = new BizVideo();
        BeanUtil.copyProperties(dto, entity);
        return R.ok(bizVideoService.save(entity));
    }

//
//    @Anonymous
//    @ApiOperation("视频分析接口")
//    @PreAuthorize("@ss.hasPermi('project:video:add')")
//    @Log(title = "视频分析接口", businessType = BusinessType.INSERT)
//    @PostMapping("/modelAnaly1")
//    public R model111(@ParameterObject BizVideoDto dto) throws IOException {
//        BizVideo video = bizVideoService.getById(dto.getVideoId());
//        if(video.getVideoId() != null && StrUtil.isNotEmpty(video.getBucket())&& StrUtil.isNotEmpty(video.getFileUrl())){
//            InputStream inputStream = uploadService.getFileInputStream(video.getFileUrl(), video.getBucket());
//            FileSystemResource fileResource = convertInputStreamToFileSystemResource(inputStream, "video.mp4");
//
//// 构造 multipart/form-data 请求
//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//            body.add("file", fileResource); // 注意字段名应与服务器端匹配
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//// 发送请求
//            RestTemplate restTemplate = new RestTemplate();
//            String uploadUrl = "http://192.168.31.155:5000/process_video/";
//            ResponseEntity<String> response = restTemplate.postForEntity(uploadUrl, requestEntity, String.class);
//
//            System.out.println(response.getBody());
//
//
//        }
//        return null;
//    }

    @ApiOperation("视频分析接口-传文件")
    @PreAuthorize("@ss.hasPermi('project:video:add')")
    @Log(title = "视频分析接口-传文件", businessType = BusinessType.INSERT)
    @PostMapping("/modelAnalyFile")
    public R modelAnalyFile(@RequestPart("file") MultipartFile file) throws IOException {
        String uploadUrl = configService.selectConfigByKey(ModelFlaskConstant.pre_model_url);
        uploadUrl = uploadUrl + ModelFlaskConstant.process_video;
        Assert.isTrue(StrUtil.isNotEmpty(uploadUrl),"没有获取到视频分析网址地址,请检查参数配置:video.model.url");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("video", file); // file 是必须的
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000); // 30秒
        factory.setReadTimeout(300000);   // 5分钟
        restTemplate.setRequestFactory(factory);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        String apiResult = restTemplate.postForObject(uploadUrl, requestEntity, String.class);
        System.out.println("response = " + apiResult);
        String taskId = JSONUtil.parseObj(apiResult).getStr("task_id");
        // 把folder和key拼接成完整的Redis键名
        String redisKey = ModelFlaskConstant.ai_model_folder  + taskId;

        // 保存到 Redis
//        redisCache.opsForValue().set(redisKey, value);
        redisCache.setCacheObject( redisKey,ModelFlaskConstant.ai_model_pending,10,TimeUnit.DAYS);
        return R.ok(taskId);
    }




//    @Anonymous
    @ApiOperation("视频分析接口")
    @PreAuthorize("@ss.hasPermi('project:video:add')")
    @Log(title = "视频分析接口", businessType = BusinessType.INSERT)
    @PostMapping("/xxxxxx")
    public R modelAnaly() throws IOException {

        iRyTask.ai_model();
        return null;
//        redisCache.setCacheObject( ModelFlaskConstant.ai_model_folder+"ssssxxx","down", 10, TimeUnit.MINUTES);
//
//        Collection<String> keys = redisCache.keys(ModelFlaskConstant.ai_model_folder+"*");
//
//        List<AiModelStatus> list = new ArrayList<>();
//        for (String key : keys) {
//            String status = redisCache.getCacheObject(key);
//            AiModelStatus aiModelStatus = new AiModelStatus();
//            aiModelStatus.setStatus(status).setTaskId(key);
//            list.add(aiModelStatus);
//        }
//        return R.ok(list);
    }

    /**
     * 新增工程视频
     */
//    @Anonymous
    @ApiOperation("视频分析接口")
    @PreAuthorize("@ss.hasPermi('project:video:add')")
    @Log(title = "视频分析接口", businessType = BusinessType.INSERT)
    @PostMapping("/modelAnaly")
    public R modelAnaly(@ParameterObject BizVideoDto dto) throws IOException {
        BizVideo video = bizVideoService.getById(dto.getVideoId());
        if(video.getVideoId() != null && StrUtil.isNotEmpty(video.getBucket())&& StrUtil.isNotEmpty(video.getFileUrl())){
            String uploadUrl = configService.selectConfigByKey(ModelFlaskConstant.pre_model_url);
            uploadUrl = uploadUrl + ModelFlaskConstant.process_video;
            Assert.isTrue(StrUtil.isNotEmpty(uploadUrl),"没有获取到视频分析网址地址,请检查参数配置:video.model.url");
            InputStream inputStream = uploadService.getFileInputStream(video.getFileName(), video.getBucket());
//            Assert.isTrue(inputStream.available() != 0,"视频为空");
            FileSystemResource fileResource = convertInputStreamToFileSystemResource(inputStream,video.getFileName() );
            // 3. 构造 multipart/form-data 请求
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("video", fileResource); // file 是必须的
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(30000); // 30秒
            factory.setReadTimeout(300000);   // 5分钟
            restTemplate.setRequestFactory(factory);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            String apiResult = restTemplate.postForObject(uploadUrl, requestEntity, String.class);
            System.out.println("response = " + apiResult);
            String taskId = JSONUtil.parseObj(apiResult).getStr("task_id");
            String redisKey = ModelFlaskConstant.ai_model_folder  + taskId;
            redisCache.setCacheObject( redisKey,ModelFlaskConstant.ai_model_pending,10,TimeUnit.DAYS);

            return R.ok(taskId);
        }
        return R.fail("未查询到视频");
    }



    public  FileSystemResource convertInputStreamToFileSystemResource(InputStream inputStream, String fileName) throws IOException {
        // 创建临时文件
        File tempFile = File.createTempFile("upload_", "_" + fileName);
        tempFile.deleteOnExit(); // JVM 退出时删除临时文件

        // 将 InputStream 的内容写入临时文件
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // 创建 FileSystemResource
        return new FileSystemResource(tempFile);
    }
    /**
     * 修改工程视频
     */
    @ApiOperation("修改工程视频")
    @PreAuthorize("@ss.hasPermi('project:video:edit')")
    @Log(title = "工程视频", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BizVideoDto dto) {
        BizVideo entity = new BizVideo();
        BeanUtil.copyProperties(dto, entity);
        return R.ok(bizVideoService.updateById(entity));
    }

    /**
     * 删除工程视频
     */
    @ApiOperation("删除工程视频")
    @PreAuthorize("@ss.hasPermi('project:video:remove')")
    @Log(title = "工程视频", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{videoId}")
    public R removeOne(@PathVariable Long videoId) {
        UpdateWrapper<BizVideo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(BizVideo::getVideoId, videoId)
                .set(BizVideo::getDelFlag, BizBaseConstant.DELFLAG_Y);
        return R.ok(bizVideoService.update(null,updateWrapper));
    }

    /**
     * 删除工程视频
     */
    @ApiOperation("删除工程视频")
    @PreAuthorize("@ss.hasPermi('project:video:remove')")
    @Log(title = "工程视频", businessType = BusinessType.DELETE)
	@DeleteMapping("/{videoIds}")
    public R remove(@PathVariable Long[] videoIds) {
        UpdateWrapper<BizVideo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(BizVideo::getVideoId, videoIds)
                .set(BizVideo::getDelFlag, BizBaseConstant.DELFLAG_Y);
        return R.ok(bizVideoService.update(null,updateWrapper));
    }


    public static void main(String[] args) {

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(30000); // 30秒
            factory.setReadTimeout(300000);   // 5分钟
            RestTemplate restTemplate = new RestTemplate(factory);
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
//            parts.add("bucketName", "yuantest");
            parts.add("video", new FileSystemResource("C:\\Users\\ASUS\\Desktop\\冲击地压工程系统\\yolo\\output3103.mp4"));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            String apiResult = restTemplate.postForObject("http://192.168.31.155:5000/process_video", parts, String.class);
            System.out.println(apiResult.toString());

    }


}
