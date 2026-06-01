package com.tujuhsembilan.glucoseclamp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionInfusionMonitoringRequest {
    @NotNull(message = "Kolom time tidak boleh kosong")
    private LocalDateTime time;

    @NotNull(message = "Kolom glucoseValue tidak boleh kosong")
    private BigDecimal glucoseValue;

    private BigDecimal confirmationRateMinKg;
    private BigDecimal rateMinKg;
    private BigDecimal flowRateMlHr;
    private String adjustmentNote;
}