package com.tujuhsembilan.glucoseclamp.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodSampleRequest {

    @Schema(description = "Activity ID yang terkait", example = "ACT-003-T-30-1")
    @NotBlank(message = "activity_id is required")
    private Long activityId;

    @Schema(description = "ID user yang mengumpulkan sample", example = "4")
    @NotNull(message = "collected_by is required")
    private Integer collectedBy;

    @Schema(description = "Waktu sample (ISO format)", example = "2026-05-21T07:10:00")
    private String sampleTime;

    @Schema(description = "Tipe sample", example = "Glucose")
    @NotBlank(message = "sample_type is required")
    private String sampleType;

    @Schema(description = "Tipe tabung", example = "Fluoride")
    @NotBlank(message = "tube_type is required")
    private String tubeType;

    @Schema(description = "Volume dalam mL", example = "3")
    @NotNull(message = "volume_ml is required")
    private Integer volumeMl;

    @NotNull(message = "lab_results list is required")
    private List<LabResultDetails> labResults;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LabResultDetails {
        @Schema(description = "Parameter name", example = "Glucose")
        @NotBlank(message = "parameter_name is required")
        private String parameterName;

        @Schema(description = "Value", example = "90")
        @NotNull(message = "value is required")
        private java.math.BigDecimal value;

        @Schema(description = "Unit", example = "mg/dL")
        private String unit;
    }
}
