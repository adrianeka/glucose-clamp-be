package com.tujuhsembilan.bookrecipe.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	private String ingridient;
	private String howToCook;
	private Boolean isDeleted;
	private String createdBy;
	private Timestamp createdTime;
	private String modifiedBy;
	private Timestamp modifiedTime;
}
