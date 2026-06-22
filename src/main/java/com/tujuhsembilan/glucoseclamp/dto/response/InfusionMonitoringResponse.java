package com.tujuhsembilan.glucoseclamp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfusionMonitoringResponse {
    private String infusionId;
    private Long sessionId;
    private LocalDateTime time;
    private BigDecimal glucoseValue;
    private BigDecimal confirmationRateMinKg;
    private BigDecimal rateMinKg;
    private BigDecimal flowRateMlHr;
    private String adjustmentNote;
    private Integer monitoredBy;
    private String status;
    private LocalDateTime createdAt;
    private Integer createdBy;
    private LocalDateTime updatedAt;
    private Integer updatedBy;
    private LocalDateTime deletedAt;
    private Integer deletedBy;
}
