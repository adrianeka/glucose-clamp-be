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

    @JsonProperty("sampling_schedule_id")
    @NotBlank(message = "sampling_schedule_id is required")
    private String samplingScheduleId;

    @JsonProperty("protocol_id")
    private String protocolId;

    @JsonProperty("phase_code")
    @NotBlank(message = "phase_code is required")
    private String phaseCode;

    @JsonProperty("time_interval")
    @NotNull(message = "time_interval is required")
    private Integer timeInterval;

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
