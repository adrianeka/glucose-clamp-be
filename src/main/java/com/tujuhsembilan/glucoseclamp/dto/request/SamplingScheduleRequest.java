package com.tujuhsembilan.glucoseclamp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SamplingScheduleRequest {

    @JsonProperty("protocol_id")
    private Long protocolId;

    @JsonProperty("phase_code")
    @NotBlank(message = "phase_code is required")
    private String phaseCode;

    @JsonProperty("phase_name")
    @NotBlank(message = "phase_name is required")
    private String phaseName;

    @JsonProperty("phase_type")
    @NotBlank(message = "phase_type is required")
    private String phaseType;

    @JsonProperty("time_interval")
    @NotNull(message = "time_interval is required")
    private Integer timeInterval;

    @JsonProperty("label_prefix")
    @NotNull(message = "label_prefix is required")
    private String labelPrefix;

    @JsonProperty("phase_duration")
    @NotNull(message = "phase_duration is required")
    private Integer phaseDuration;

    @JsonProperty("blood_raw")
    @NotNull(message = "blood_raw is required")
    private Boolean bloodRaw;

    @JsonProperty("insulin_inject")
    @NotNull(message = "insulin_inject is required")
    private Boolean insulinInject;

    @JsonProperty("pk_sample_collection")
    @NotNull(message = "pk_sample_collection is required")
    private Boolean pkSampleCollection;
}
