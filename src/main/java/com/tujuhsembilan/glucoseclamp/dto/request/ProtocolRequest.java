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

    @JsonProperty("protocol_id")
    @NotBlank(message = "protocol_id is required")
    private String protocolId;

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

    @JsonProperty("duration_hours")
    private BigDecimal durationHours;

    @JsonProperty("version")
    @NotNull(message = "version is required")
    private Float version;

    @JsonProperty("protocol_details")
    private List<ProtocolDetailRequest> protocolDetails;
}
