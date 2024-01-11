package com.tujuhsembilan.bookrecipe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeResponseDTO {
	private int recipeId;
	private RecipeCategoryDTO categories;
	private RecipeLevelDTO levels;
	private String recipeName;
	private String imageUrl;
	private Integer time;
	private Boolean isFavorite;
}
