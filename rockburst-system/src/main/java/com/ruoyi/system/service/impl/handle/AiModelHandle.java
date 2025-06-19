package com.ruoyi.system.service.impl.handle;


import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.system.constant.ModelFlaskConstant;
import com.ruoyi.system.domain.BizVideo;
import com.ruoyi.system.domain.SysFileInfo;
import com.ruoyi.system.domain.aimodel.TaskStatus;
import com.ruoyi.system.domain.aimodel.TaskStatusResponse;
import com.ruoyi.system.mapper.BizVideoMapper;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.SysFileInfoService;
import com.ruoyi.system.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * 地址构造复杂程度
 * wdl
 */
@Slf4j
@Service
public class AiModelHandle  {

	@Resource
	private RestTemplate restTemplate;

	@Autowired
	private RedisCache redisCache;

	@Autowired
	private ISysConfigService configService;

	@Autowired
	private SysFileInfoService sysFileInfoService;

	@Autowired
	private UploadService uploadService;

	@Autowired
	private BizVideoMapper bizVideoMapper;


	/**
	 * 视频分析
	 * @param fileId
	 * @return
	 * @throws IOException
	 */
	public String modelAnalyByFileId(Long videoId , String bucketName, String url, String fileName) throws IOException {
		if(StrUtil.isNotEmpty(bucketName)&& StrUtil.isNotEmpty(url)){
			String uploadUrl = configService.selectConfigByKey(ModelFlaskConstant.pre_model_url);
			uploadUrl = uploadUrl + ModelFlaskConstant.process_video;
			Assert.isTrue(StrUtil.isNotEmpty(uploadUrl),"没有获取到视频分析网址地址,请检查参数配置:video.model.url");
			InputStream inputStream = uploadService.getFileInputStream(fileName, bucketName);
//			Assert.isTrue(inputStream.available() != 0,"视频为空");
			FileSystemResource fileResource = convertInputStreamToFileSystemResource(inputStream,fileName );
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
			log.info("ai视频分析任务:",apiResult);
			System.out.println("response = " + apiResult);
			String taskId = JSONUtil.parseObj(apiResult).getStr("task_id");
			String redisKey = ModelFlaskConstant.ai_model_folder  + taskId;
			redisCache.setCacheObject( redisKey,ModelFlaskConstant.ai_model_pending,10,TimeUnit.DAYS);

			if(videoId != null){
				BizVideo bizVideo = new BizVideo();
				bizVideo.setVideoId(videoId).setTaskId(taskId);
				bizVideoMapper.updateById(bizVideo);
			}

			return taskId;
		}
		return null;
	}


	/**
	 * 获取视频分析服务的任务list
	 * @return
	 */
	public TaskStatusResponse getTaskList() {
		String uploadUrl = configService.selectConfigByKey(ModelFlaskConstant.pre_model_url);
		uploadUrl = uploadUrl + ModelFlaskConstant.task_status_list;
		Assert.isTrue(StrUtil.isNotEmpty(uploadUrl),"没有获取到视频分析网址地址,请检查参数配置:video.model.url");
		TaskStatusResponse response  = restTemplate.getForObject(uploadUrl, TaskStatusResponse.class);
		return response;
	}



	/**
	 * 更新 redis Task列表
	 * @param response
	 * @return
	 */
	public TaskStatusResponse updateTaskRedis(TaskStatusResponse response) {
		if (response != null && response.getTasks() != null) {
			response.getTasks().forEach((taskId, taskStatus) -> {
				JSONObject jsonObject = new JSONObject();
				jsonObject.set("status", taskStatus.getStatus());
				jsonObject.set("output_path", taskStatus.getOutput_path());
				redisCache.setCacheObject( ModelFlaskConstant.ai_model_folder+taskId,jsonObject.toString(),10, TimeUnit.DAYS);
			});
		}
		return response;
	}

	/**
	 * 查询redis task状态
	 * @param taskId
	 * @return
	 */
	public TaskStatus getRedisTaskIdStatus(String taskId) {
		String  tasksuatusstr   = redisCache.getCacheObject(ModelFlaskConstant.ai_model_folder+taskId);
		if(StrUtil.isNotEmpty(tasksuatusstr)){
			TaskStatus taskStatus = JSONUtil.toBean(tasksuatusstr,TaskStatus.class);
			return taskStatus;
		}
		return null;
	}

//	/**
//	 * 获取视频ai 分析视频url
//	 * @param taskId
//	 * @return
//	 */
//	public TaskStatus getDownUrl(String taskId) {
//		String  tasksuatusstr   = redisCache.getCacheObject(ModelFlaskConstant.ai_model_folder+taskId);
//		if(StrUtil.isEmpty(tasksuatusstr)){
//			TaskStatus taskStatus = JSONUtil.toBean(tasksuatusstr,TaskStatus.class);
//			return taskStatus;
//		}
//		return null;
//	}

	/**
	 * 上传网络视频到minio
	 * @param bucketName
	 * @param url
	 * @return
	 */
	public SysFileInfo uploadVieoMinio(String bucketName, String url) {
		MultipartFile file = urlToMultipartFile(url);
		SysFileInfo sysFileInfo = sysFileInfoService.upload(file,bucketName,null);
		return sysFileInfo;
	}

	public SysFileInfo uploadimageMinio(MultipartFile multipartFile , String bucketName) {
		SysFileInfo sysFileInfo = sysFileInfoService.upload(multipartFile,bucketName,null);
		return sysFileInfo;
	}



	/**
	 * 创建 FileSystemResource
	 * @param inputStream
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public FileSystemResource convertInputStreamToFileSystemResource(InputStream inputStream, String fileName) throws IOException {
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
	 * 将网络文件转换为文件流
	 * @param fileUrl
	 * @return
	 */
	public static MultipartFile urlToMultipartFile(String fileUrl) {
		try {
			// 直接通过 Hutool 获取输入流
			InputStream inputStream = HttpUtil.createGet(fileUrl).execute().bodyStream();

			// 截取文件名
			String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

			// 这里可以根据需要固定 contentType，也可以动态获取
			String contentType = "video/mp4"; // 你可以自己设为 "video/mp4"，更保险

			// 创建 MultipartFile
			MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, contentType, IoUtil.readBytes(inputStream));

			IoUtil.close(inputStream); // 关闭流
			return multipartFile;

		} catch (Exception e) {
			throw new RuntimeException("文件转换失败", e);
		}
	}

}
