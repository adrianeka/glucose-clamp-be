package com.tujuhsembilan.bookrecipe.dto.request;

import com.tujuhsembilan.bookrecipe.dto.CategoriesDTO;
import com.tujuhsembilan.bookrecipe.dto.LevelsDTO;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRecipeRequest {

    @NotNull
    private CategoriesDTO categories;

    @NotNull
    private LevelsDTO levels;

    private int UserId;

    @NotBlank(message = "Kolom recipeName tidak boleh kosong")
    @Size(min = 1, max = 255, message = "Panjang kolom tidak boleh melebihi 255 karakter")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Kolom tidak boleh berisi special character/angka")
    private String recipeName;

    private String imageFilename;

    @NotNull(message = "Kolom timeCook tidak boleh kosong")
    @Min(value = 1, message = "Kolom hanya boleh berisi angka 1-999")
    @Max(value = 999, message = "Kolom hanya boleh berisi angka 1-999")
    private Integer timeCook;

    @NotBlank(message = "Kolom ingredient tidak boleh kosong")
    @Size(min = 1)
    private String ingredient;

    @NotBlank(message = "Kolom howToCook tidak boleh kosong")
    @Size(min = 1)
    private String howToCook;

    @Builder.Default
    private Boolean isDeleted = false;

    private String createdBy;

    @Builder.Default
    private Timestamp createdTime = new Timestamp(System.currentTimeMillis());

    private String modifiedBy;

    @Builder.Default
    private Timestamp modifiedTime = new Timestamp(System.currentTimeMillis());
}