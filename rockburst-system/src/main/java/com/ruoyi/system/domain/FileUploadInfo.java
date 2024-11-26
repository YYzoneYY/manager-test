package com.ruoyi.system.domain;


import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class FileUploadInfo {

    //@NotBlank(message = "文件名不能为空")
    private String fileName;

    // @NotNull(message = "文件大小不能为空")
    private Double fileSize;

    // @NotBlank(message = "Content-Type不能为空")
    private String contentType;

    //  @NotNull(message = "分片数量不能为空")
    private Integer partCount;

    // @NotBlank(message = "uploadId 不能为空")
    private String uploadId;

    // 桶名称
    //private String bucketName;

    //md5
    private String fileMd5;

    //文件类型
    private String fileType;


    public FileUploadInfo() {
    }


}

