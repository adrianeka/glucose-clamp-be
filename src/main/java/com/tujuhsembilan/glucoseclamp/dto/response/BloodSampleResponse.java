package com.tujuhsembilan.glucoseclamp.dto.response;

import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodSampleResponse {
    private String bloodSampleId;
    private String activityId;
    private String sampleCode;
    private Integer collectedBy;
    private LocalDateTime sampleTime;
    private String sampleType;
    private String tubeType;
    private Integer volumeMl;
    private String status;
    private LocalDateTime createdAt;
    private Integer createdBy;
    private LocalDateTime updatedAt;
    private Integer updatedBy;
    private LocalDateTime deletedAt;
    private Integer deletedBy;
}
