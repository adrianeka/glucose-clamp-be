package com.tujuhsembilan.glucoseclamp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProtocolRequest {

    @JsonProperty("protocol_code")
    @NotBlank(message = "protocol_code is required")
    private String protocolCode;

    @JsonProperty("protocol_name")
    @NotBlank(message = "protocol_name is required")
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
    @NotNull(message = "version is required")
    private Float version;

    @JsonProperty("sampling_schedules")
    private List<SamplingScheduleRequest> samplingSchedules;
}
