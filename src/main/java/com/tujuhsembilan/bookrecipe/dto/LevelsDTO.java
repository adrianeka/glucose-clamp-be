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

public class LevelsDTO {
	private int levelId;
	private String levelName;
	private Boolean isDeleted;
	private String createdBy;
	private Timestamp createdTime;
	private String modifiedBy;
	private Timestamp modifiedTime;
}
