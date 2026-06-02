package com.tujuhsembilan.glucoseclamp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodSampleRequest {

    @Schema(description = "Activity ID yang terkait", example = "ACT-001-T-30-101")
    @NotBlank(message = "activity_id is required")
    private String activityId;

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
}
