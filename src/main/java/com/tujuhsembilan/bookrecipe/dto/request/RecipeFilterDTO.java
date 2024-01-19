package com.tujuhsembilan.bookrecipe.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RecipeFilterDTO {
    @NotBlank(message = "UserId Cannot Empty")
    private int userId;
    private String recipeName;
    private Integer levelId;
    private Integer categoryId;
    private Integer time;
}
