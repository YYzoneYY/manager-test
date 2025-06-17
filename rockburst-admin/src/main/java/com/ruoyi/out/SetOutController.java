package com.ruoyi.out;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.ObjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.push.GeTuiUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizDangerAreaDto;
import com.ruoyi.system.domain.vo.BizDangerAreaVo;
import com.ruoyi.system.domain.vo.BizPresetPointVo;
import com.ruoyi.system.domain.vo.BizWorkfaceSvgVo;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * configContour
 *
 * @author ruoyi
 * @date 2024-11-09
 */
@Api(tags = "basic-危险区")
@RestController
@RequestMapping("/configContour")
public class SetOutController extends BaseController
{
    @Autowired
    private IBizProjectRecordService bizProjectRecordService;
    private final String fastApiBaseUrl = "http://localhost:8000"; // 替换为实际 IP
    @Autowired
    private RedisCache redisCache;
    // 上传到服务器的目标路径（确保有写权限）
    private static final String UPLOAD_DIR = "D:/upload/"; // Windows 示例路径

    @ApiOperation("查询危险区管理列表")
    @GetMapping("/up")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件为空");
        }

        try {
            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            // 拼接保存路径
            String filePath = UPLOAD_DIR + System.currentTimeMillis() + "_" + originalFilename;
            // 保存文件
            file.transferTo(new File(filePath));

            return ResponseEntity.ok("上传成功: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("上传失败");
        }
    }
    @ApiOperation("shangc")
    @PostMapping("/uploadAndForward")
    public String uploadAndForward(@RequestPart("file") MultipartFile file) throws Exception {
        // 1. 临时保存用户上传的文件
        String tempPath = "D:/" + file.getOriginalFilename(); // 注意路径权限
        File tempFile = new File(tempPath);
        file.transferTo(tempFile);

        // 2. 调用上传服务
//        ExternalFileUploadService service = new ExternalFileUploadService();
        String result = uploadFileToExternalServer(tempPath);

        // 3. 删除临时文件
        tempFile.delete();


        ContourRequest req = new ContourRequest();
        req.setFile_name(file.getOriginalFilename());
        String sx = "";
        FastApiCaller fastApiCaller = new FastApiCaller();
        req.setLayer_names(Arrays.asList("等值线", "等值线注记"));
        String sx1 = fastApiCaller.callContourDxf(fastApiBaseUrl, req);
        redisCache.setCacheObject("contour_result", sx1);

        req.setLayer_names(Arrays.asList("0-断层上", "0-断层下"));
        String sx2 = fastApiCaller.callBigFault(fastApiBaseUrl, req);
        redisCache.setCacheObject("big_fault_result", sx2);

        req.setLayer_names(Arrays.asList("0-断层小"));
        String sx3 = fastApiCaller.callSmallFault(fastApiBaseUrl, req);
        redisCache.setCacheObject("small_fault_result", sx3);

        req.setLayer_names(Arrays.asList("0-采空区"));
        String sx4 = fastApiCaller.callGob(fastApiBaseUrl, req);
        redisCache.setCacheObject("gob_result", sx4);
        return sx;
    }

    public String uploadFileToExternalServer(String filePath) {
        String url = "http://localhost:8000/big-fault-dxf-save/"; // 外部接口地址

        // 1. 构建文件资源
        File file = new File(filePath);
        if (!file.exists()) {
            return "文件不存在";
        }

        FileSystemResource resource = new FileSystemResource(file);

        // 2. 构建请求体
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource); // 外部接口字段是 file，如果不是请改成实际字段名

        // 3. 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 4. 发送请求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        return response.getBody(); // 返回结果
    }


//    @GetMapping("/draw-all")
    @ApiOperation("draw-all")
    @PostMapping("/draw-all")
    public ResponseEntity<byte[]> drawAll(@RequestBody DrawAll all) {
        try {

            // 1. 从 Redis 取 JSON 字符串
            String bigJson = redisCache.getCacheObject("big_fault_result");
            String smallJson = redisCache.getCacheObject("small_fault_result");
            String gobJson = redisCache.getCacheObject("gob_result");
            String contourJson = redisCache.getCacheObject("contour_result");

            List<Map> bigData = JSONUtil.toList(new JSONArray(bigJson), Map.class);
            List<Map> smallData = JSONUtil.toList(new JSONArray(smallJson), Map.class);
            List<Map> gobData = JSONUtil.toList(new JSONArray(gobJson), Map.class);
            List<Map> contourData = JSONUtil.toList(new JSONArray(contourJson), Map.class);

            Map<String, Object> request = new HashMap<>();

            request.put("big_data", bigData);
//            request.put("small_data", smallData);
            request.put("gob_data", gobData);
            request.put("contour_data", contourData);


            request.put("xmin", 19363737.718);
            request.put("xmax", 19371371.734);
            request.put("ymin", 4305393.654);
            request.put("ymax", 4317845.213);
            request.put("multiple", 0.01);
            request.put("start_level", 20);
            request.put("end_level", 200);
            request.put("level", 25);

            // 请求绘图服务
            byte[] imageBytes = callDrawAllAndGetStream("http://127.0.0.1:8000/draw-all/", request);

            // 构建响应
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDisposition(ContentDisposition.inline().filename("result.png").build());

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public byte[]  callDrawAllAndGetStream(String url, Map<String, Object> requestJson) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestJson, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                byte[].class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new IOException("请求失败，状态码：" + response.getStatusCode());
        }
    }

    public void callDrawAllAndSaveImage(String url, Map<String, Object> requestJson, String savePath) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestJson, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                byte[].class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            Files.write(Paths.get(savePath), response.getBody());
            System.out.println("图像保存成功：" + savePath);
        } else {
            throw new IOException("请求失败，状态码：" + response.getStatusCode());
        }
    }

}
