package com.tujuhsembilan.bookrecipe.spesification.filter;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RecipeFilter {
    private String level;
    private String category;
    private Integer cookMin;
    private Integer cookMax;
    private Integer sort;
}
