package com.tujuhsembilan.bookrecipe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyRecipeResDTO {
	private int recipeId;
	private MyRecipeCategoriesDTO categories;
	private MyRecipesLevelsDTO levels;
	private String recipeName;
	private String imageUrl;
	private int time;
	private Boolean isFavorite;
}
