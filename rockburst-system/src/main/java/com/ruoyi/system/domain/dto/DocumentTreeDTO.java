package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/20
 * @description:
 */
@Data
@ApiModel("树形结构")
@AllArgsConstructor
@NoArgsConstructor
public class DocumentTreeDTO {

    private String label;
    private String value;
    private String documentName;
    private Long fileId;
    private Long createTime;
    private Integer level;
    private Long sort;
    private boolean disable;
    private List<DocumentTreeDTO> children;

    public static <T> List<DocumentTreeDTO> treeRecurrence(T dataId, List<? extends NewTreeEntity<?,T>> treeEntity) {
        List<DocumentTreeDTO> documentTreeDTOS = new ArrayList<>();

        for (NewTreeEntity<?,T> tTreeEntity : treeEntity) {
            if (dataId.equals(tTreeEntity.getSuperId())) {
                documentTreeDTOS.add(new DocumentTreeDTO(tTreeEntity.getLabel(), String.valueOf(tTreeEntity.getValue()),
                        String.valueOf(tTreeEntity.documentName()), tTreeEntity.fileId(), tTreeEntity.createTime(),
                        tTreeEntity.level(), tTreeEntity.sort(), tTreeEntity.isDisable(),
                        treeRecurrence(tTreeEntity.getValue(), treeEntity)));
            }
        }
        if (!documentTreeDTOS.isEmpty()) {
            return documentTreeDTOS;
        }
        return null;
    }
}