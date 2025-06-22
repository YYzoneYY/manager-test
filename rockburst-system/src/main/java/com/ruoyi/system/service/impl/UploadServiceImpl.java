package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.FileUploadInfo;
import com.ruoyi.system.domain.SysFileInfo;
import com.ruoyi.system.domain.utils.MinioUtils;
import com.ruoyi.system.domain.utils.RedisRepo;
import com.ruoyi.system.domain.utils.ResponseResult;
import com.ruoyi.system.domain.utils.ResultCode;
import com.ruoyi.system.mapper.SysFileInfoMapper;
import com.ruoyi.system.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.ruoyi.system.domain.utils.ResultCode.ACCESS_PARAMETER_INVALID;


@Slf4j
@Service
public class UploadServiceImpl implements UploadService {


    @Resource
    private MinioUtils fileService;

    @Resource
    private RedisRepo redisRepo;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    SysFileInfoMapper sysFileInfoMapper;


    /**
     * 通过 md5 获取已上传的数据（断点续传）
     *
     * @param md5 String
     * @return Mono<Map < String, Object>>
     */
    @Override
    public ResponseResult<Object> getByFileMd5(String md5) {
        Boolean s = StringUtils.hasText(md5);
        if (!StringUtils.hasText(md5)) {

            log.error("查询文件是否存在、入参无效");
            return ResponseResult.error(ACCESS_PARAMETER_INVALID);
        }

        log.info("tip message: 通过 <{}> 查询数据是否存在", md5);

        // 获取文件名称和id
        String value = redisRepo.get(md5);

        FileUploadInfo fileUploadInfo = null;

        if (StringUtils.hasText(value)) {
            fileUploadInfo = JSONObject.parseObject(value, FileUploadInfo.class);
        }

        if (Objects.isNull(fileUploadInfo)) {
            // 返回数据不存在
            log.error("error message: 文件数据不存在");
            return ResponseResult.error(ResultCode.FOUND);
        }

        // 获取桶名称
        String bucketName = fileService.getBucketName(fileUploadInfo.getBucketName());

        return fileService.getByFileMd5(fileUploadInfo.getFileName(), fileUploadInfo.getUploadId(), bucketName);
    }

    @Override
    public InputStream getFileInputStream(String fileName, String bucketName) {

        return fileService.getFileInputStream(fileName,bucketName);
    }

    /**
     * 文件分片上传
     *
     * @param fileUploadInfo
     * @return Mono<Map < String, Object>>
     */
    @Override
    public ResponseResult<Object> initMultiPartUpload(FileUploadInfo fileUploadInfo) {

        log.info("tip message: 通过 <{}> 开始初始化<分片上传>任务", fileUploadInfo);

        // 获取文件桶名
        String bucketName = fileUploadInfo.getBucketName();

        // 单文件上传可拆分，可直接上传完成
        if (fileUploadInfo.getPartCount() == 1) {

            log.info("tip message: 当前分片数量 <{}> 进行单文件上传", fileUploadInfo.getPartCount());

            // 获取文件分片上传的url
            return fileService.getUploadObjectUrl(fileUploadInfo.getFileName(), bucketName);

        }else {

            log.info("tip message: 当前分片数量 <{}> 进行分片上传", fileUploadInfo.getPartCount());

            // 获取文件分片上传的url
            return fileService.initMultiPartUpload(fileUploadInfo, fileUploadInfo.getFileName(), fileUploadInfo.getPartCount(), fileUploadInfo.getContentType(), bucketName);
        }

    }

    /**
     * 文件合并
     *
     * @param
     * @return boolean
     */
    @Override
    public ResponseResult mergeMultipartUpload(FileUploadInfo fileUploadInfo) throws Exception {

        log.info("tip message: 通过 <{}> 开始合并<分片上传>任务", fileUploadInfo);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        // 获取桶名称
        String bucketName = fileUploadInfo.getBucketName();

        // 获取合并结果
        boolean result = fileService.mergeMultipartUpload(fileUploadInfo.getFileName(), fileUploadInfo.getUploadId(), bucketName);


        SysFileInfo sysFileInfo = new SysFileInfo();
        sysFileInfo.setFileOldName(fileUploadInfo.getFileName());
        String fileName = fileUploadInfo.getFileName();
        sysFileInfo.setFileNewName(fileName.substring(fileName.lastIndexOf("/") + 1));
        sysFileInfo.setFilePath(fileName.substring(0, fileName.lastIndexOf("/") + 1));
        sysFileInfo.setFileSuffix(fileUploadInfo.getFileType());
        sysFileInfo.setFileSize(fileUploadInfo.getFileSize().longValue());
        sysFileInfo.setBucketName(fileUploadInfo.getBucketName());
        Long ts = System.currentTimeMillis();
        sysFileInfo.setCreateTime(ts);
        sysFileInfo.setUpdateTime(ts);
        sysFileInfo.setCreateBy(currentUser.getUserId());
        sysFileInfo.setUpdateBy(currentUser.getUserId());
        //获取上传文件地址
        if(result){
            String filePath = fileService.getFilePath(fileUploadInfo.getBucketName().toLowerCase(), fileUploadInfo.getFileName());
            sysFileInfo.setFileUrl(filePath);
            InputStream inputStream = this.getFileInputStream(sysFileInfo.getFileNewName(), bucketName);
            int[] resolution = getVideoResolution(inputStream);
            if (sysFileInfoMapper.insert(sysFileInfo) < 1) {
                throw new RuntimeException("文件信息插入库失败!");
            }

            CompletableFuture.supplyAsync(() -> {
                try {
                    asyncUpload(sysFileInfo.getFileNewName(),fileName,bucketName);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            });
//            asyncUpload(sysFileInfo.getFileNewName(),sysFileInfo.getFileNewName(),bucketName);
//            asyncUpload(sysFileInfo.getFileNewName(), fileName, bucketName)
//                    .thenAccept(result1 -> {
//                        System.out.println("上传成功: " + result1);
//                    })
//                    .exceptionally(ex -> {
//                        System.err.println("上传失败: " + ex.getMessage());
//                        return null;
//                    });

            //下载 大视频 传到 155
//            InputStream inputStream = this.getFileInputStream(filePath, bucketName);
//            FileSystemResource fileResource = convertInputStreamToFileSystemResource(inputStream,fileName );
//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//            body.add("file", fileResource); // file 是必须的
//            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//            factory.setConnectTimeout(30000); // 30秒
//            factory.setReadTimeout(300000);   // 5分钟
//            restTemplate.setRequestFactory(factory);
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//            String apiResult = restTemplate.postForObject("http://192.168.31.155:7000/upload", requestEntity, String.class);
            SysFileInfo ssa =  sysFileInfoMapper.selectById(sysFileInfo.getFileId());
            Map<String,Object> map = BeanUtil.beanToMap(ssa);
            map.put("resolution",resolution);
            return ResponseResult.success(map);
//            return ResponseResult.success(filePath);
        }

        log.error("error message: 文件合并异常");

        return  ResponseResult.error();
    }

    public CompletableFuture<String> asyncUpload(String filePath, String fileName, String bucketName) {
        File tempFile = null;

        try {
            InputStream inputStream = this.getFileInputStream(filePath, bucketName);
            FileSystemResource fileResource = convertInputStreamToFileSystemResource(inputStream, fileName);
            tempFile = fileResource.getFile(); // 获取实际的临时文件

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(30000);
            factory.setReadTimeout(300000);
            restTemplate.setRequestFactory(factory);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            String apiResult = restTemplate.postForObject("http://192.168.31.155:7000/upload", requestEntity, String.class);
            return CompletableFuture.completedFuture(apiResult);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        } finally {
            // ✅ 上传完成或失败后删除临时文件
            if (tempFile != null && tempFile.exists()) {
                boolean deleted = tempFile.delete();
                System.out.println("上传完成，临时文件已删除: " + deleted);
            }
        }
    }


//    public CompletableFuture<String> asyncUpload(String filePath, String fileName, String bucketName) {
//        try {
//            InputStream inputStream = this.getFileInputStream(filePath, bucketName);
//            FileSystemResource fileResource = convertInputStreamToFileSystemResource(inputStream, fileName);
//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//            body.add("file", fileResource);
//
//            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//            factory.setConnectTimeout(30000);
//            factory.setReadTimeout(300000);
//            restTemplate.setRequestFactory(factory);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//            String apiResult = restTemplate.postForObject("http://192.168.31.155:7000/upload", requestEntity, String.class);
//            return CompletableFuture.completedFuture(apiResult);
//        } catch (Exception e) {
//            return CompletableFuture.failedFuture(e);
//        }
//    }

    public static int[] getVideoResolution(InputStream inputStream) throws Exception {
        // 将 InputStream 写入临时文件
        File tempVideo = File.createTempFile("temp_video", ".mp4");
        tempVideo.deleteOnExit();

        try (OutputStream out = new FileOutputStream(tempVideo)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        // 使用 JavaCV 读取临时文件的视频信息
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(tempVideo)) {
            grabber.start();
            int width = grabber.getImageWidth();
            int height = grabber.getImageHeight();
            grabber.stop();
            return new int[]{width, height};
        }
    }

    public FileSystemResource convertInputStreamToFileSystemResource(InputStream inputStream, String fileName) throws IOException {
        // 指定临时文件目录 + 指定文件名
        File tempFile = File.createTempFile("upload_", "_" + fileName);
        try (OutputStream outStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        }
        // ✅ 重命名为你想要的名字（保留扩展名）
        File renamedFile = new File(tempFile.getParent(), fileName);
        if (renamedFile.exists()) {
            renamedFile.delete(); // 删除已存在的
        }
        boolean success = tempFile.renameTo(renamedFile);
        if (!success) {
            throw new IOException("重命名文件失败");
        }

        return new FileSystemResource(renamedFile);
    }

    /**
     * 创建 FileSystemResource
     * @param inputStream
     * @param fileName
     * @return
     * @throws IOException
     */
    public FileSystemResource convertInputStreamToFileSystemResource1(InputStream inputStream, String fileName) throws IOException {
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


    @Override
    public ResponseResult<Object> mergeMultipartUploadNoToken(FileUploadInfo fileUploadInfo) {

        log.info("tip message: 通过 <{}> 开始合并<分片上传>任务", fileUploadInfo);
        // 获取桶名称
        String bucketName = fileUploadInfo.getBucketName();

        // 获取合并结果
        boolean result = fileService.mergeMultipartUpload(fileUploadInfo.getFileName(), fileUploadInfo.getUploadId(), bucketName);


        SysFileInfo sysFileInfo = new SysFileInfo();
        sysFileInfo.setFileOldName(fileUploadInfo.getFileName());
        String fileName = fileUploadInfo.getFileName();
        sysFileInfo.setFileNewName(fileName.substring(fileName.lastIndexOf("/") + 1));
        sysFileInfo.setFilePath(fileName.substring(0, fileName.lastIndexOf("/") + 1));
        sysFileInfo.setFileSuffix(fileUploadInfo.getFileType());
        sysFileInfo.setFileSize(fileUploadInfo.getFileSize().longValue());
        sysFileInfo.setBucketName(fileUploadInfo.getBucketName());
        Long ts = System.currentTimeMillis();
        sysFileInfo.setCreateTime(ts);
        sysFileInfo.setUpdateTime(ts);
//        sysFileInfo.setCreateBy(fileUploadInfo.getImme());
//        sysFileInfo.setUpdateBy(fileUploadInfo.getImme());
        //获取上传文件地址
        if(result){
            String filePath = fileService.getFilePath(fileUploadInfo.getBucketName().toLowerCase(), fileUploadInfo.getFileName());
            sysFileInfo.setFileUrl(filePath);
            if (sysFileInfoMapper.insert(sysFileInfo) < 1) {
                throw new RuntimeException("文件信息插入库失败!");
            }
            return ResponseResult.success(sysFileInfoMapper.selectById(sysFileInfo.getFileId()));
//            return ResponseResult.success(filePath);
        }

        log.error("error message: 文件合并异常");

        return  ResponseResult.error();    }

    @Override
    public String getFilePath(String bucketName, String fileName) {
        return fileService.getFilePath(bucketName, fileName);
    }


    @Override
    public String upload(MultipartFile file, String bucketName) {
        fileService.upload(file, bucketName);
        return getFilePath(bucketName, file.getName());
    }

    public ResponseResult fileIsExits(FileUploadInfo fileUploadInfo){
        boolean b = fileService.doesObjectExist(fileUploadInfo.getBucketName(), fileUploadInfo.getFileName());

        if(b){
            return ResponseResult.success();
        }

        return  ResponseResult.error();
    }


}
