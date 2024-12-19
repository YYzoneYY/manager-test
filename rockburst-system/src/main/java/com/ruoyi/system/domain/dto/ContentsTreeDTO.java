package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/12/18
 * @description:
 */

@Data
@ApiModel("层级树形结构")
@AllArgsConstructor
@NoArgsConstructor
public class ContentsTreeDTO {

    private String label;
    private String value;
    private boolean disable;
    private List<ContentsTreeDTO> children;

    public static <T> List<ContentsTreeDTO> treeRecursive(T id, List<? extends ContentsTreeEntity<?, T>> contentsTreeEntity) {
        List<ContentsTreeDTO> contentsTreeDTOS = new ArrayList<>();

        for (ContentsTreeEntity<?, T> tContentsTreeEntity : contentsTreeEntity) {
            if (id.equals(tContentsTreeEntity.getSuperId())) {
                contentsTreeDTOS.add(new ContentsTreeDTO(tContentsTreeEntity.getLabel(), String.valueOf(tContentsTreeEntity.getValue()),
                        tContentsTreeEntity.isDisable(), treeRecursive(tContentsTreeEntity.getValue(), contentsTreeEntity)));
            }
        }
        if (contentsTreeDTOS.size() > 0) {
            return contentsTreeDTOS;
        }
        return null;
    }
}