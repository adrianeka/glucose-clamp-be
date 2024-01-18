package com.tujuhsembilan.bookrecipe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeFilterRequestDTO {
    private Integer userId;
    private String recipeName;
    private Integer levelId;
    private Integer categoryId;
    private String time;
}
