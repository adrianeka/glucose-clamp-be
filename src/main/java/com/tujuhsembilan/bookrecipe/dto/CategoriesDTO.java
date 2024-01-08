package com.tujuhsembilan.bookrecipe.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriesDTO {
    private int categoryId;
	private String categoryName;
	// private Boolean isDeleted;
	// private String createdBy;
	// private Timestamp createdTime;
	// private String modifiedBy;
	// private Timestamp modifiedTime;
}
