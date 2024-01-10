package com.tujuhsembilan.bookrecipe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeCategoryDTO {
	private int categoryId;
	private String categoryName;
}