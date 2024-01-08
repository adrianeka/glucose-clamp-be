package com.tujuhsembilan.bookrecipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

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
