package com.tujuhsembilan.bookrecipe.service.specification.filter;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RecipeFilter {
    @NotNull
    @NotBlank(message = "UserId Cannot Empty")
    private int userId;
    private String recipeName;
    private String level;
    private String category;
    private Integer cookMin;
    private Integer cookMax;
    private Integer sort;
}
