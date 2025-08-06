package com.ruoyi.out;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * configContour
 *
 * @author ruoyi
 * @date 2024-11-09
 */
@Api(tags = "basic-危险区111")
@RestController
@RequestMapping("/configContour")
public class SetOutController extends BaseController
{
    @Autowired
    private IBizProjectRecordService bizProjectRecordService;
//    private final String fastApiBaseUrl = "http://127.0.0.1:8000"; // 替换为实际 IP
    private final String fastApiBaseUrl = "http://192.168.31.156:38000"; // 替换为实际 IP

    // 上传到服务器的目标路径（确保有写权限）
    private static final String UPLOAD_DIR = "/home/imgfask/dxf_to_contour_map/"; // Windows 示例路径
//    private static final String UPLOAD_DIR = "D:\\PycharmProjects\\"; // Windows 示例路径

    @Autowired
    private SysDxfConfigMapper dxfConfigMapper;

    @Autowired
    private SysDxfEntityMapper dxfEntityMapper;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    @Qualifier("redisByteTemplate")
    public RedisTemplate<String, byte[]> redisByteTemplate;




    @ApiOperation("shangc")
    @PostMapping("/uploadAndForward")
    public String uploadAndForward(@RequestPart("file") MultipartFile file) throws Exception {
        // 1. 临时保存用户上传的文件
        String tempPath = UPLOAD_DIR + file.getOriginalFilename(); // 注意路径权限
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

        String contour_layers = getconfigbykey("contour_config");
        req.setLayer_names(JSONUtil.parseArray(contour_layers).toList(String.class));
        String sx1 = fastApiCaller.callContourDxf(fastApiBaseUrl, req);
//        redisCache.setCacheObject("contour_result", sx1);
        setvalueBykey("contour_result",sx1);

        String big_layers = getconfigbykey("big_fault_config");
        req.setLayer_names(JSONUtil.parseArray(big_layers).toList(String.class));
        String sx2 = fastApiCaller.callBigFault(fastApiBaseUrl, req);
//        redisCache.setCacheObject("big_fault_result", sx2);
        setvalueBykey("big_fault_result",sx2);
//
        String small_layers = getconfigbykey("small_fault_config");
        req.setLayer_names(JSONUtil.parseArray(small_layers).toList(String.class));
        String sx3 = fastApiCaller.callSmallFault(fastApiBaseUrl, req);
//        redisCache.setCacheObject("small_fault_result", sx3);
        setvalueBykey("small_fault_result",sx3);
//
        String gob_layers = getconfigbykey("gob_config");
        req.setLayer_names(JSONUtil.parseArray(gob_layers).toList(String.class));
        String sx4 = fastApiCaller.callGob(fastApiBaseUrl, req);
//        redisCache.setCacheObject("gob_result", sx4);
        setvalueBykey("gob_result",sx4);

        String fold_layers = getconfigbykey("fold_config");
        req.setLayer_names(JSONUtil.parseArray(fold_layers).toList(String.class));
        String sx5 = fastApiCaller.callFold(fastApiBaseUrl, req);
//        redisCache.setCacheObject("gob_result", sx4);
        setvalueBykey("fold_result",sx5);

        String coal_pillar_layers = getconfigbykey("coal_pillar_config");
        req.setLayer_names(JSONUtil.parseArray(coal_pillar_layers).toList(String.class));
        String sx6 = fastApiCaller.callCoalPillar(fastApiBaseUrl, req);
//        redisCache.setCacheObject("gob_result", sx4);
        setvalueBykey("coal_pillar_result",sx6);
        return sx;
    }

    public String uploadFileToExternalServer(String filePath) {
        String url = fastApiBaseUrl+"/big-fault-dxf-save/"; // 外部接口地址

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

//    @GetMapping("/get-cropped-image")
    @ApiOperation("get-cropped-image")
    @GetMapping("/get-cropped-image")
    public ResponseEntity<byte[]> getCroppedImage(  @RequestParam(required = false, defaultValue = "0") Integer x_min,
                                                    @RequestParam(required = false, defaultValue = "0") Integer y_max,
                                                    @RequestParam(required = false, defaultValue = "0") Integer x_max,
                                                    @RequestParam(required = false, defaultValue = "0") Integer y_min) {
        String fastApiUrl = String.format(fastApiBaseUrl+"/crop-image"+"?x_min=%d&y_max=%d&x_max=%d&y_min=%d",
                x_min, y_max, x_max, y_min);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<byte[]> response = restTemplate.getForEntity(fastApiUrl, byte[].class);

// 检查 FastAPI 是否返回成功
        if (response.getStatusCode() == HttpStatus.OK) {
            byte[] imageBytes = response.getBody();

            // 动态获取 FastAPI 返回的 Content-Type，如果无效则默认 PNG
            MediaType contentType = response.getHeaders().getContentType();
            if (contentType == null) {
                contentType = MediaType.IMAGE_PNG; // 默认值
            }

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(contentType);
            headers.setContentLength(imageBytes.length);
            headers.setContentDisposition(ContentDisposition.inline().filename("cropped.png").build());

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } else {
            // 如果 FastAPI 返回错误，直接转发错误信息
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }
    }


    public byte[] getCroppedImage1( Double x_min, Double y_max, Double x_max, Double y_min, String type) {

        //获取图像大小
        String josn = getconfigbykey("config");
        DrawAll alll  =  JSONUtil.toBean(josn,DrawAll.class);
        if(x_max  >= alll.getXmax() || x_min  <= alll.getXmin() || y_max  >= alll.getYmax() || y_min  <= alll.getYmin()){
            // 比矿图大
        }
        //计算裁剪 参数
        int minx = (int) (x_min - alll.getXmin());
        int miny = (int) (y_min - alll.getYmin());
        int maxx = (int) (alll.getXmax() - x_max);
        int maxy = (int) (alll.getYmax() - y_max);

        String fastApiUrl = String.format(fastApiBaseUrl+"/crop-image"+"?x_min=%d&y_max=%d&x_max=%d&y_min=%d&dxf_type=%s",
                minx, maxy, maxx, miny,type);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<byte[]> response = restTemplate.getForEntity(fastApiUrl, byte[].class);

// 检查 FastAPI 是否返回成功
        if (response.getStatusCode() == HttpStatus.OK) {
            byte[] imageBytes = response.getBody();


            return imageBytes;
        } else {
            // 如果 FastAPI 返回错误，直接转发错误信息
            return null;
        }
    }


    public  double calculateArea(double xMin, double yMax, double xMax, double yMin) {
        double width = Math.abs(xMax - xMin);
        double height = Math.abs(yMax - yMin);
        return width * height;
    }


    // 获取规则
    @ApiOperation("rules-get")
    @GetMapping("/rules/{ruleName}")
    public ResponseEntity<?> getRule(@PathVariable String ruleName) {
        try {
            String url = fastApiBaseUrl + "/rules/" + ruleName;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到规则：" + ruleName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("调用失败：" + e.getMessage());
        }
    }

    // 更新规则
    @ApiOperation("rules-update")
    @PostMapping("/rules")
    public ResponseEntity<?> updateRule( @RequestBody RuleData ruleData) {
        try {
            String url = fastApiBaseUrl + "/rules/" + ruleData.getRuleName();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if("FAULT_DROP_RULES".equals(ruleData.getRuleName())){
                HttpEntity<Object> request = new HttpEntity<>(ruleData.getRules(), headers);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
                return ResponseEntity.ok(response.getBody());
            }
            HttpEntity<Object> request = new HttpEntity<>(ruleData.getRuleData(), headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("get-cropped-image1")
    @GetMapping("/get-cropped-image1")
    public ResponseEntity<byte[]> getCroppedImageCute(@ParameterObject CutImage cutImage) {
        System.out.println("values = " + cutImage);
        String url = fastApiBaseUrl + "/cut-image/" ;

        // 构建带列表参数的 URL
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        cutImage.getShapes().forEach(shape -> builder.queryParam("shapes", shape));
        cutImage.getPoints().forEach(p -> builder.queryParam("points",p ));

        URI uri = builder.build().encode().toUri();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.getForEntity(uri, byte[].class);

        // 检查 FastAPI 是否返回成功
        if (response.getStatusCode() == HttpStatus.OK) {
            byte[] imageBytes = response.getBody();

            // 动态获取 FastAPI 返回的 Content-Type，如果无效则默认 PNG
            MediaType contentType = response.getHeaders().getContentType();
            if (contentType == null) {
                contentType = MediaType.IMAGE_PNG; // 默认值
            }

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(contentType);
            headers.setContentLength(imageBytes.length);
            headers.setContentDisposition(ContentDisposition.inline().filename("cropped.png").build());

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } else {
            // 如果 FastAPI 返回错误，直接转发错误信息
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }

    }




    //    @GetMapping("/draw-all")
    @ApiOperation("draw-all-colorbar")
    @PostMapping("/draw-all-colorbar")
    public ResponseEntity<byte[]> drawAllColorbar(@RequestBody DrawAll all) {
        Map<String, Object> request = new HashMap<>();
        String josn = getconfigbykey("config");

        try {


            Map<String,Object> map =  JSONUtil.toBean(josn,Map.class);
            request.putAll(map);
            if(all.getXmin() != null ){
                request.put("xmin", all.getXmin());
                request.put("xmax", all.getXmax());
                request.put("ymin", all.getYmin());
                request.put("ymax", all.getYmax());
                request.put("multiple", all.getMultiple());
                request.put("start_level", all.getStartLevel());
                request.put("end_level", all.getEndLevel());
                request.put("level", all.getLevel());

            }

            String bigJson = "";
            String smallJson = "";
            String gobJson = "";
            String contourJson = "";

            List<Map> bigData = new ArrayList<>();
            List<Map> smallData = new ArrayList<>();
            List<Map> gobData = new ArrayList<>();
            List<Map> contourData = new ArrayList<>();
            List<Map> foldData= new ArrayList<>();
            List<Map> coalPillarData= new ArrayList<>();
            request.put("big_data", bigData);
            request.put("small_data", smallData);
            request.put("gob_data", gobData);
            request.put("contour_data", contourData);
            request.put("fold_data", foldData);
            request.put("coal_pillar_data", coalPillarData);


            if(all != null && all.getDraws() != null && all.getDraws().size() > 0){
                for (Draw draw : all.getDraws()) {
                    if(draw.getName().equals("big_fault_result")){
                        getentitybykey("big_fault_result");
//                        bigJson = redisCache.getCacheObject("big_fault_result");
                        bigJson = getentitybykey("big_fault_result");
                        bigData = JSONUtil.toList(new JSONArray(bigJson), Map.class);
                        request.put("big_data", bigData);

                    }
                    if(draw.getName().equals("small_fault_result")){
//                        smallJson = redisCache.getCacheObject("small_fault_result");
                        smallJson = getentitybykey("small_fault_result");

                        smallData = JSONUtil.toList(new JSONArray(smallJson), Map.class);
                        request.put("small_data", smallData);

                    }
                    if(draw.getName().equals("gob_result")){
//                        gobJson = redisCache.getCacheObject("gob_result");
                        gobJson = getentitybykey("gob_result");

                        gobData = JSONUtil.toList(new JSONArray(gobJson), Map.class);
                        request.put("gob_data", gobData);

                    }
                    if(draw.getName().equals("contour_result")){
//                        contourJson = redisCache.getCacheObject("contour_result");
                        contourJson = getentitybykey("contour_result");

                        contourData = JSONUtil.toList(new JSONArray(contourJson), Map.class);
                        request.put("contour_data", contourData);

                    }
                    if(draw.getName().equals("fold_result")){
//                        contourJson = redisCache.getCacheObject("contour_result");
                        contourJson = getentitybykey("fold_result");

                        contourData = JSONUtil.toList(new JSONArray(contourJson), Map.class);
                        request.put("fold_data", contourData);

                    }if(draw.getName().equals("coal_pillar_result")){
//                        contourJson = redisCache.getCacheObject("contour_result");
                        contourJson = getentitybykey("coal_pillar_result");

                        contourData = JSONUtil.toList(new JSONArray(contourJson), Map.class);
                        request.put("coal_pillar_data", contourData);

                    }
                }
            }

            // 请求绘图服务
            byte[] imageBytes = callDrawAllAndGetStream(fastApiBaseUrl+"/draw-all-colorbar/", request);

//            redisByteTemplate.opsForValue().set("all", imageBytes);
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



    //    @GetMapping("/draw-all")
    @ApiOperation("draw-all")
    @PostMapping("/draw-all")
    public ResponseEntity<byte[]> drawAll(@RequestBody DrawAll all) {
        Map<String, Object> request = new HashMap<>();
        String josn = getconfigbykey("config");

//        if(all.getXmin() != null){
//            double get_area  = calculateArea(all.getXmin(),all.getYmax(),all.getXmax(),all.getYmin());
//
//            DrawAll aa = JSONUtil.toBean(josn,DrawAll.class);
//            double org_area  = calculateArea(aa.getXmin(),aa.getYmax(),aa.getXmax(),aa.getYmin());
//
//            if(org_area/get_area <= 5 && org_area/get_area >= 1){
//                String type = "";
//                for (Draw draw : all.getDraws()) {
//                    if(StrUtil.isNotEmpty( draw.getName()) && !draw.getName().equals("[]")){
//                        type = draw.getName();
//                    }
//                }
//                byte[] sstypes = getCroppedImage1(all.getXmin(), all.getYmax(), all.getXmax(), all.getYmin(),type);
//
////                redisByteTemplate.opsForValue().set("all", sstypes);
//                // 构建响应
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.IMAGE_PNG);
//                headers.setContentDisposition(ContentDisposition.inline().filename("result.png").build());
//                return new ResponseEntity<>(sstypes, headers, HttpStatus.OK);
//            }
//
//        }else if(all.getXmax() == null) {
//            String type = "";
//            for (Draw draw : all.getDraws()) {
//                if(StrUtil.isNotEmpty( draw.getName()) && !draw.getName().equals("[]")){
//                    type = draw.getName();
//                }
//            }
//            String fastApiUrl = String.format(fastApiBaseUrl+"/get-image"+"?dxf_type=%s", type);
//            RestTemplate restTemplate = new RestTemplate();
//            ResponseEntity<byte[]> response = restTemplate.getForEntity(fastApiUrl, byte[].class);
//            // 如果成功，则返回图片流
//            if (response.getStatusCode() == HttpStatus.OK) {
//                byte[] imageBytes = response.getBody();
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.IMAGE_PNG);
//                headers.setContentDisposition(ContentDisposition.inline().filename("result.png").build());
//                return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
//            }
//        }

        try {


            Map<String,Object> map =  JSONUtil.toBean(josn,Map.class);
            request.putAll(map);
            if(all.getXmin() != null ){
                request.put("xmin", all.getXmin());
                request.put("xmax", all.getXmax());
                request.put("ymin", all.getYmin());
                request.put("ymax", all.getYmax());
                request.put("multiple", all.getMultiple());
                request.put("start_level", all.getStartLevel());
                request.put("end_level", all.getEndLevel());
                request.put("level", all.getLevel());

            }

            String bigJson = "";
            String smallJson = "";
            String gobJson = "";
            String contourJson = "";

            List<Map> bigData = new ArrayList<>();
            List<Map> smallData = new ArrayList<>();
            List<Map> gobData = new ArrayList<>();
            List<Map> contourData = new ArrayList<>();
            List<Map> foldData= new ArrayList<>();
            List<Map> coalPillarData= new ArrayList<>();
            request.put("big_data", bigData);
            request.put("small_data", smallData);
            request.put("gob_data", gobData);
            request.put("contour_data", contourData);
            request.put("fold_data", foldData);
            request.put("coal_pillar_data", coalPillarData);


            if(all != null && all.getDraws() != null && all.getDraws().size() > 0){
                for (Draw draw : all.getDraws()) {
                    if(draw.getName().equals("big_fault_result")){
                        getentitybykey("big_fault_result");
//                        bigJson = redisCache.getCacheObject("big_fault_result");
                        bigJson = getentitybykey("big_fault_result");
                        bigData = JSONUtil.toList(new JSONArray(bigJson), Map.class);
                        request.put("big_data", bigData);

                    }
                    if(draw.getName().equals("small_fault_result")){
//                        smallJson = redisCache.getCacheObject("small_fault_result");
                        smallJson = getentitybykey("small_fault_result");

                        smallData = JSONUtil.toList(new JSONArray(smallJson), Map.class);
                        request.put("small_data", smallData);

                    }
                    if(draw.getName().equals("gob_result")){
//                        gobJson = redisCache.getCacheObject("gob_result");
                        gobJson = getentitybykey("gob_result");

                        gobData = JSONUtil.toList(new JSONArray(gobJson), Map.class);
                        request.put("gob_data", gobData);

                    }
                    if(draw.getName().equals("contour_result")){
//                        contourJson = redisCache.getCacheObject("contour_result");
                        contourJson = getentitybykey("contour_result");

                        contourData = JSONUtil.toList(new JSONArray(contourJson), Map.class);
                        request.put("contour_data", contourData);

                    }
                    if(draw.getName().equals("fold_result")){
//                        contourJson = redisCache.getCacheObject("contour_result");
                        contourJson = getentitybykey("fold_result");

                        contourData = JSONUtil.toList(new JSONArray(contourJson), Map.class);
                        request.put("fold_data", contourData);

                    }if(draw.getName().equals("coal_pillar_result")){
//                        contourJson = redisCache.getCacheObject("contour_result");
                        contourJson = getentitybykey("coal_pillar_result");

                        contourData = JSONUtil.toList(new JSONArray(contourJson), Map.class);
                        request.put("coal_pillar_data", contourData);

                    }
                }
            }

            // 请求绘图服务
            byte[] imageBytes = callDrawAllAndGetStream(fastApiBaseUrl+"/draw-all/", request);

//            redisByteTemplate.opsForValue().set("all", imageBytes);
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

    public String getconfigbykey(String key){
        Map<String, Object> request = new HashMap<>();
        QueryWrapper<SysDxfConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysDxfConfig::getDxfKey,key);
        List<SysDxfConfig> ss = dxfConfigMapper.selectList(queryWrapper);
        if(ss != null && ss.size() > 0){
            return ss.get(0).getDxfValue();
        }
        return null;
    }

    public String getentitybykey(String key){
        Map<String, Object> request = new HashMap<>();
        QueryWrapper<SysDxfEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysDxfEntity::getDxfKey,key);
        List<SysDxfEntity> ss = dxfEntityMapper.selectList(queryWrapper);
        if(ss != null && ss.size() > 0){
            return ss.get(0).getDxfValue();
        }
        return null;
    }


    public void setvalueBykey(String key,String value ){
        SysDxfEntity sysDxfEntity = new SysDxfEntity();
        sysDxfEntity.setDxfKey(key).setDxfValue(value);
        QueryWrapper<SysDxfEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysDxfEntity::getDxfKey,key);
        dxfEntityMapper.delete(queryWrapper);
        dxfEntityMapper.insert(sysDxfEntity);
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
