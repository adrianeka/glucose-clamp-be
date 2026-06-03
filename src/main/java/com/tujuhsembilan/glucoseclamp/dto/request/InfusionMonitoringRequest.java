package com.tujuhsembilan.glucoseclamp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfusionMonitoringRequest {

    @Schema(description = "Session ID ", example = "101")
    @NotNull(message = "session_id is required")
    private Integer sessionId;

    @Schema(description = "Time of monitoring in ISO 8601 format", example = "2024-06-01T14:30:00")
    @NotNull(message = "time is required")
    private String time;

    @Schema(description = "Glucose value measured during monitoring", example = "120.50")
    @NotNull(message = "glucose_value is required")
    private BigDecimal glucoseValue;

    @Schema(description = "Confirmation rate per minute per kg", example = "0.05")
    private BigDecimal confirmationRateMinKg;

    @Schema(description = "Rate per minute per kg", example = "0.10")
    private BigDecimal rateMinKg;

    @Schema(description = "Flow rate in ml/hr", example = "50.00")
    private BigDecimal flowRateMlHr;

    @Schema(description = "Adjustment note for the infusion monitoring", example = "Adjusted based on glucose value")
    private String adjustmentNote;

    @Schema(description = "ID of the user who monitored", example = "5")
    private Integer monitoredBy;
}
