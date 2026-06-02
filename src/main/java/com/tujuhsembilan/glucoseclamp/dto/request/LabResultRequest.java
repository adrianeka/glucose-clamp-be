package com.tujuhsembilan.glucoseclamp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabResultRequest {

    @JsonProperty("lab_result_id")
    @NotBlank(message = "lab_result_id is required")
    private String labResultId;

    @JsonProperty("blood_sample_id")
    @NotBlank(message = "blood_sample_id is required")
    private String bloodSampleId;

    @JsonProperty("parameter_name")
    @NotBlank(message = "parameter_name is required")
    private String parameterName;

    @JsonProperty("verified_by")
    private Integer verifiedBy;

    @JsonProperty("value")
    @NotNull(message = "value is required")
    private BigDecimal value;

    @JsonProperty("reference_range_min")
    @NotNull(message = "reference_range_min is required")
    private BigDecimal referenceRangeMin;

    @JsonProperty("reference_range_max")
    @NotNull(message = "reference_range_max is required")
    private BigDecimal referenceRangeMax;

    @JsonProperty("unit")
    @NotBlank(message = "unit is required")
    private String unit;

    @JsonProperty("abnormal_flag")
    @NotBlank(message = "abnormal_flag is required")
    private String abnormalFlag;
}
