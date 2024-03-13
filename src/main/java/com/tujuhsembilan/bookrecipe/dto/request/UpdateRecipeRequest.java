package com.tujuhsembilan.bookrecipe.dto.request;

import com.tujuhsembilan.bookrecipe.dto.CategoriesDTO;
import com.tujuhsembilan.bookrecipe.dto.LevelsDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRecipeRequest {

    private int recipeId;

    private CategoriesDTO categories;

    private LevelsDTO levels;

    private int UserId;

    @Size(min = 1, max = 255, message = "Panjang kolom tidak boleh melebihi 255 karakter")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Kolom tidak boleh berisi special character/angka")
    private String recipeName;

    private String imageFilename;

    @Min(value = 1, message = "Kolom hanya boleh berisi angka 1-999")
    @Max(value = 999, message = "Kolom hanya boleh berisi angka 1-999")
    private Integer timeCook;

    @Size(min = 1)
    private String ingredient;

    @Size(min = 1)
    private String howToCook;

    private Boolean isDeleted;

    private String createdBy;

    private Timestamp createdTime;

    private String modifiedBy;

    private Timestamp modifiedTime;
}
