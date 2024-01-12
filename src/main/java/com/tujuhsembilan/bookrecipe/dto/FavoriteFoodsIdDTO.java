package com.tujuhsembilan.bookrecipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteFoodsIdDTO {
    private String createdBy;
    private Timestamp createdTime;
    private Boolean isFavorite;
    private String modifiedBy;
    private Timestamp modifiedTime;
    private Integer recipeId;
    private Integer userId;
}
