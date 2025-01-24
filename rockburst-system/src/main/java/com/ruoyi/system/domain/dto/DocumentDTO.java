package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.Entity.ConstructDocumentEntity;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/1/24
 * @description:
 */

@Data
public class DocumentDTO extends ConstructDocumentEntity {

    private String fileUrl;
}