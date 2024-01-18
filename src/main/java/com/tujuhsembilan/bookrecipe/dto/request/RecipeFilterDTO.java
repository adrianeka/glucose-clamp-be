package com.tujuhsembilan.bookrecipe.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RecipeFilterDTO {
    @NotNull
    @NotBlank(message = "UserId Cannot Empty")
    private Integer userId;
    private String recipeName;
    private Integer levelId;
    private Integer categoryId;
    private Integer time;
}
