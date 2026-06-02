package com.tujuhsembilan.glucoseclamp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabResultResponse {

    @JsonProperty("lab_result_id")
    private String labResultId;

    @JsonProperty("blood_sample_id")
    private String bloodSampleId;

    @JsonProperty("parameter_name")
    private String parameterName;

    @JsonProperty("verified_by")
    private Integer verifiedBy;

    @JsonProperty("value")
    private BigDecimal value;

    @JsonProperty("reference_range_min")
    private BigDecimal referenceRangeMin;

    @JsonProperty("reference_range_max")
    private BigDecimal referenceRangeMax;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("abnormal_flag")
    private String abnormalFlag;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("created_by")
    private Integer createdBy;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("updated_by")
    private Integer updatedBy;

    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;

    @JsonProperty("deleted_by")
    private Integer deletedBy;

    @JsonProperty("status")
    private String status;
}
