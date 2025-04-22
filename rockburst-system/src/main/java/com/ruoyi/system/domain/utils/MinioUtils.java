package com.ruoyi.system.domain.utils;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.HashMultimap;
import com.ruoyi.system.domain.FileUploadInfo;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Part;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ruoyi.system.domain.utils.ResultCode.DATA_NOT_EXISTS;
import static com.ruoyi.system.domain.utils.ResultCode.UPLOAD_FILE_FAILED;


@Slf4j
@Component
public class MinioUtils {

    @Value(value = "${minio.endpoint}")
    private String endpoint;
    @Value(value = "${minio.accesskey}")
    private String accesskey;
    @Value(value = "${minio.secretkey}")
    private String secretkey;

    @Resource
    private RedisRepo redisRepo;

    private CustomMinioClient customMinioClient;


    /**
     * 用spring的自动注入会注入失败
     */
    @PostConstruct
    public void init() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accesskey, secretkey)
                .build();
        customMinioClient = new CustomMinioClient(minioClient);
    }


    /**
     * 单文件签名上传
     *
     * @param objectName 文件全路径名称
     * @param bucketName 桶名称
     * @return /
     */
    public ResponseResult<Object> getUploadObjectUrl(String objectName, String bucketName) {

        log.info("tip message: 通过 <{}-{}> 开始单文件上传<minio>", objectName, bucketName);

        try {
            String url = getPresidedObjectUrl(bucketName, objectName);

            Map<String, Object> resMap = new HashMap<>();

            resMap.put("uploadId", "SingleFileUpload");

            resMap.put("urlList", Collections.singletonList(url));

            return ResponseResult.success(resMap);

        } catch (Exception e) {

            log.error("error message: 初始化分片上传失败、原因:", e);

            // 返回 文件上传失败
            return ResponseResult.error(UPLOAD_FILE_FAILED);
        }

    }



    /**
     * 文件分片上传
     *
     * @param fileUploadInfo
     * @param objectName     文件全路径名称
     * @param partCount      分片数量
     * @param contentType    类型，如果类型使用默认流会导致无法预览
     * @param bucketName     桶名称
     * @return Mono<Map < String, Object>>
     */

    public ResponseResult<Object> initMultiPartUpload(FileUploadInfo fileUploadInfo, String objectName, int partCount, String contentType, String bucketName) {
        log.info("tip message: 通过 <{}-{}-{}-{}> 开始初始化<分片上传>数据", objectName, partCount, contentType, bucketName);

        try {
            String uploadId = getUploadId(bucketName, objectName, contentType);

            fileUploadInfo.setUploadId(uploadId);

            //redis保存文件信息
            redisRepo.saveTimeout(fileUploadInfo.getFileMd5(), JSONObject.toJSONString(fileUploadInfo), 7, TimeUnit.DAYS);

            List<String> partList = getPartUploadUrls(uploadId, partCount, bucketName, objectName);

            Map<String, Object> resMap = new HashMap<>();

            resMap.put("uploadId", uploadId);

            resMap.put("urlList", partList);

            log.info("tip message: 文件初始化<分片上传>、成功");

            return ResponseResult.success(resMap);

        } catch (Exception e) {

            log.error("error message: 初始化分片上传失败、原因:", e);

            // 返回 文件上传失败
            return ResponseResult.error(UPLOAD_FILE_FAILED);
        }
    }


    /**
     * 分片上传完后合并
     *
     * @param objectName 文件全路径名称
     * @param uploadId   返回的uploadId
     * @param bucketName 桶名称
     * @return boolean
     */
    public boolean mergeMultipartUpload(String objectName, String uploadId, String bucketName) {
        try {
            log.info("tip message: 通过 <{}-{}-{}> 合并<分片上传>数据", objectName, uploadId, bucketName);
            //目前仅做了最大1000分片
            Part[] parts = new Part[1000];
            // 查询上传后的分片数据
            ListPartsResponse partResult = customMinioClient.listMultipart(bucketName, null, objectName, 1000, 0, uploadId, null, null);
            int partNumber = 1;
            for (Part part : partResult.result().partList()) {
                parts[partNumber - 1] = new Part(partNumber, part.etag());
                partNumber++;
            }
            // 合并分片
            customMinioClient.mergeMultipartUpload(bucketName, null, objectName, uploadId, parts, null, null);

        } catch (Exception e) {
            log.error("error message: 合并失败、原因:", e);
            return false;
        }
        return true;
    }

    /**
     * 通过 sha256 获取上传中的分片信息
     *
     * @param objectName 文件全路径名称
     * @param uploadId   返回的uploadId
     * @param bucketName 桶名称
     * @return Mono<Map < String, Object>>
     */
    public ResponseResult<Object> getByFileMd5(String objectName, String uploadId, String bucketName) {
        log.info("通过 <{}-{}-{}> 查询<minio>上传分片数据", objectName, uploadId, bucketName);
        try {
            // 查询上传后的分片数据
            ListPartsResponse partResult = customMinioClient.listMultipart(bucketName, null, objectName, 1000, 0, uploadId, null, null);
            List<Integer> collect = partResult.result().partList().stream().map(Part::partNumber).collect(Collectors.toList());
            return ResponseResult.uploading(collect);
        } catch (Exception e) {
            log.error("error message: 查询上传后的分片信息失败、原因:", e);
            return ResponseResult.error(DATA_NOT_EXISTS);
        }
    }

    /**
     * 获取文件下载地址
     *
     * @param bucketName 桶名称
     * @param fileName   文件名
     * @return
     */
    public String getFilePath(String bucketName, String fileName) {
        return StrUtil.format("{}/{}/{}", endpoint, bucketName, fileName);//文件访问路径
    }

    /**
     * 创建一个桶
     *
     * @return
     */
    public String createBucket(String bucketName) {
        try {
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();

            //如果桶存在
            if (customMinioClient.bucketExists(bucketExistsArgs)) {
                return bucketName;
            }

            // 如果不存在则创建新文件桶
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(bucketName).build();

            customMinioClient.makeBucket(makeBucketArgs);

            return bucketName;

        } catch (Exception e) {
            log.error("创建桶失败：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    /**
     * 根据文件类型获取minio桶名称
     *
     * @param fileType
     * @return
     */
    public String getBucketName(String fileType) {
        try {
            if (StringUtils.isNotEmpty(fileType)) {

                //判断桶是否存在
                String bucketName = createBucket(fileType.toLowerCase());

                if (StringUtils.isNotEmpty(bucketName)) {

                    return bucketName;
                } else {

                    return fileType;
                }
            }
        } catch (Exception e) {
            log.error("Error reading bucket name ");
        }
        return fileType;
    }


    /**
     *  单文件获取上传url
     * @param bucketName
     * @param objectName
     * @return
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws XmlParserException
     * @throws InternalException
     */
    private String getPresidedObjectUrl(String bucketName, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return customMinioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(1, TimeUnit.DAYS)
                        .build());
    }


    /**
     * 获取合并id
     * @param bucketName
     * @param objectName
     * @param contentType
     * @return
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws XmlParserException
     * @throws InvalidResponseException
     * @throws InternalException
     */
    private String getUploadId(String bucketName, String objectName, String contentType) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, XmlParserException, InvalidResponseException, InternalException {

        if (CharSequenceUtil.isBlank(contentType)) {
            contentType = "application/octet-stream";
        }

        HashMultimap<String, String> headers = HashMultimap.create();

        headers.put("Content-Type", contentType);

        return customMinioClient.initMultiPartUpload(bucketName, null, objectName, headers, null);
    }

    /**
     * 获取文件分片urls
     * @param uploadId
     * @param partCount
     * @param bucketName
     * @param objectName
     * @return
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws XmlParserException
     * @throws InternalException
     */
    private List<String> getPartUploadUrls(String uploadId, int partCount, String bucketName, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<String> partList = new ArrayList<>();

        for (int i = 1; i <= partCount; i++) {

            Map<String, String> reqParams = new HashMap<>();

            reqParams.put("uploadId", uploadId);

            reqParams.put("partNumber", String.valueOf(i));

            String uploadUrl = customMinioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(1, TimeUnit.DAYS)
                            .extraQueryParams(reqParams)
                            .build());

            partList.add(uploadUrl);
        }
        return partList;
    }

    /**
     * 判断文件是否存在
     *
     * @param bucketName 存储桶
     * @param objectName 对象
     * @return true：存在
     */

    public  boolean doesObjectExist(String bucketName, String objectName) {
        boolean exist = true;
        try {
            customMinioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }


    /**
     * 文件上传
     *
     * @param file 文件
     * @return Boolean
     */
    public String upload(MultipartFile file, String bucketName) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new RuntimeException();
        }
        String objectName = file.getName();
        try {
            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(bucketName).object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build();
            //文件名称相同会覆盖
            customMinioClient.putObject(objectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        // 查看文件地址
        GetPresignedObjectUrlArgs build = new GetPresignedObjectUrlArgs().builder().bucket(bucketName).object(objectName).method(Method.GET).build();
        String url = null;
        try {
            url = customMinioClient.getPresignedObjectUrl(build);
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        }
        return url;
    }

}