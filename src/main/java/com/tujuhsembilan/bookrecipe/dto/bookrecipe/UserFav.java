package com.tujuhsembilan.bookrecipe.dto.bookrecipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFav {
    private int recipeId;
    private CategoryFav categories;
    private LevelFav levels;
    private String recipeName;
    private String imageUrl;
    private int time;
    private Boolean isFavorite;

}
