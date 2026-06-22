package com.tujuhsembilan.glucoseclamp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SamplingScheduleResponse {

    @JsonProperty("sampling_schedule_id")
    private Long samplingScheduleId;

    @JsonProperty("protocol_id")
    private Long protocolId;

    @JsonProperty("phase_code")
    private String phaseCode;

    @JsonProperty("time_interval")
    private Integer timeInterval;

    @JsonProperty("relative_minute")
    private Integer relativeMinute;

    @JsonProperty("blood_raw")
    private Boolean bloodRaw;

    @JsonProperty("insulin_inject")
    private Boolean insulinInject;

    @JsonProperty("pk_sample_collection")
    private Boolean pkSampleCollection;

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
