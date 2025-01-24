package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.system.domain.FileUploadInfo;
import com.ruoyi.system.domain.utils.ResponseResult;
import com.ruoyi.system.service.UploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;


/**
 * minio上传流程
 *
 * 1.检查数据库中是否存在上传文件
 *
 * 2.根据文件信息初始化，获取分片预签名url地址，前端根据url地址上传文件
 *
 * 3.上传完成后，将分片上传的文件进行合并
 *
 * 4.保存文件信息到数据库
 */

@Api(tags = "大文件分片上传")
@Slf4j
@RestController
@RequestMapping("/uploadNoToken")
public class FileMinioNoTokenController {

    @Resource
    private UploadService uploadService;

//    @Resource
//    private MinioUtils minioUtils;

    /**
     * 校验文件是否存在
     *+
     * @param md5 String
     * @return ResponseResult<Object>
     */
    @Anonymous
    @ApiOperation("检查文件是否存在、是否进行断点续传")
    @GetMapping("/multipart/check")
    public ResponseResult checkFileUploadedByMd5(@RequestParam("md5") String md5) {
        log.info("REST: 通过查询 <{}> 文件是否存在、是否进行断点续传", md5);
        return uploadService.getByFileMd5(md5);
    }

    /**
     * 分片初始化
     *
     * @param fileUploadInfo 文件信息
     * @return ResponseResult<Object>
     */
    @Anonymous
    @ApiOperation("分片初始化-获取文件分片上传的url")
    @PostMapping("/multipart/init")
    public ResponseResult initMultiPartUpload(@RequestBody FileUploadInfo fileUploadInfo) {
        log.info("REST: 通过 <{}> 初始化上传任务", fileUploadInfo);
        return uploadService.initMultiPartUpload(fileUploadInfo);
    }

    /**
     * 完成上传
     *
     * @param fileUploadInfo  文件信息
     * @return ResponseResult<Object>
     */
    @Anonymous
    @ApiOperation("合并")
    @PostMapping("/multipart/merge")
    public ResponseResult completeMultiPartUpload(@RequestBody FileUploadInfo fileUploadInfo) {
        log.info("REST: 通过 <{}> 合并上传任务", fileUploadInfo);
        return uploadService.mergeMultipartUploadNoToken(fileUploadInfo);
    }

    @Anonymous
    @ApiOperation("fileIsExits")
    @PostMapping("/multipart/fileIsExits")
    public ResponseResult fileIsExits(@RequestBody FileUploadInfo fileUploadInfo) {
        log.info("REST: 通过 <{}> 判断文件是否存在", fileUploadInfo);
        return uploadService.fileIsExits(fileUploadInfo);
    }


    @Anonymous
    @ApiOperation("upload")
    @PostMapping("/multipart/upload")
    public void upload(@RequestPart("file") MultipartFile file, @RequestParam(value = "url") String url, HttpServletResponse response) throws IOException, InterruptedException {
        log.info("执行");
        log.info("REST: 通过 <{}> file", file.getSize());
        log.info("REST: 通过 <{}> url", url);
        byte[] fileBytes = file.getBytes();
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/octet-stream")
                .PUT(HttpRequest.BodyPublishers.ofByteArray(fileBytes))
                .build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        // 设置 HttpServletResponse 状态码
        response.setStatus(httpResponse.statusCode());

        // 设置 HttpServletResponse 响应头
        httpResponse.headers().map().forEach((key, values) ->
                values.forEach(value -> response.addHeader(key, value))
        );

        // 将 HttpResponse 的响应体写入到 HttpServletResponse 输出流
        response.getWriter().write(httpResponse.body());
    }

//    @Anonymous
//    @ApiOperation("createBucket")
//    @RequestMapping("/createBucket")
//    public void createBucket(@RequestParam("bucketName")String bucketName){
//        String bucket = minioUtils.createBucket(bucketName);
//    }

    public static void main(String[] args) throws IOException, InterruptedException {
//         创建 HTTP PUT 请求

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://47.92.215.128:19000/bust-dev/173768011732511395973.pdf?uploadId=Zjc0MWZjOTctOTU5Mi00OTQ2LWIyZTQtZmY4NmM4YjE4M2U0LjdmZTZlZjE3LWZhMTQtNGRkMS1hODQ5LTYzZTM2MDQ0NzBmNXgxNzM3NjgwMTE3MzU2NDExNjc4&partNumber=1&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=9nLLPYeJs3CX1jj3GC1r%2F20250124%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250124T005517Z&X-Amz-Expires=86400&X-Amz-SignedHeaders=host&X-Amz-Signature=eb35195329d52bf3109ca3925fa36258584a2b7cb7909acce66dc0c25b4c99d1"
                ))
                .header("Content-Type", "application/octet-stream")
                .PUT(HttpRequest.BodyPublishers.ofFile(Paths.get("D:\\WeChat Files\\wxid_hx6z9oiw8zi122\\FileStorage\\File\\2024-12\\7.山科大发〔2022〕21号关于印发《山东科技大学自然科学科研业绩等级认定办法（试行）》《山东科技大学人文社会科学科研业绩等级认定办法（试行）》的通知.pdf")))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        response.headers();

    }


}