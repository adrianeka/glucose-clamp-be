package com.tujuhsembilan.bookrecipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
// import java.util.Set;

// import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RecipesDTO {
    private int recipeId;
    private CategoriesDTO categories;
    private UsersDTO users;
    private LevelsDTO levels;
    private String recipeName;
    private String imageFilename;
    private Integer timeCook;
    private String ingredient;
    private String howToCook;
    private Boolean isDeleted;
    private String createdBy;
    private Timestamp createdTime;
    private String modifiedBy;
    private Timestamp modifiedTime;
    private Boolean isFavorite;
}
