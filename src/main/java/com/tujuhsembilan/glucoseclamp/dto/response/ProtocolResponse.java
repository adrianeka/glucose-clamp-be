package com.tujuhsembilan.glucoseclamp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProtocolResponse {

    @JsonProperty("protocol_id")
    private Long protocolId;

    @JsonProperty("protocol_code")
    private String protocolCode;

    @JsonProperty("protocol_name")
    private String protocolName;

    @JsonProperty("insulin_dose_rule")
    private String insulinDoseRule;

    @JsonProperty("insulin_dose_unit")
    private String insulinDoseUnit;

    @JsonProperty("glucose_target_min")
    private BigDecimal glucoseTargetMin;

    @JsonProperty("glucose_target_max")
    private BigDecimal glucoseTargetMax;

    @JsonProperty("glucose_target_unit")
    private String glucoseTargetUnit;

    @JsonProperty("glucose_target_min_extreme")
    private BigDecimal glucoseTargetMinExtreme;

    @JsonProperty("glucose_target_max_extreme")
    private BigDecimal glucoseTargetMaxExtreme;

    @JsonProperty("duration_hours")
    private BigDecimal durationHours;

    @JsonProperty("glucose_drop_trigger_percentage")
    private BigDecimal glucoseDropTriggerPercentage;

    @JsonProperty("initial_glucose_infusion_rate")
    private BigDecimal initialGlucoseInfusionRate;

    @JsonProperty("initial_glucose_infusion_rate_unit")
    private String initialGlucoseInfusionRateUnit;


    @JsonProperty("version")
    private Float version;

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

    @JsonProperty("sampling_schedules")
    // private List<SamplingScheduleResponse> samplingSchedules;
    private String samplingScheduleSummary;
}
