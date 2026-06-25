package com.tujuhsembilan.glucoseclamp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfusionMonitoringResponse {
    private String infusionId;
    private Long sessionId;
    private String protocolCode;
    private String protocolName;
    private LocalDateTime time;
    private BigDecimal glucoseValue;
    private BigDecimal confirmationRateMinKg;
    private BigDecimal rateMinKg;
    private BigDecimal flowRateMlHr;
    private String adjustmentNote;
    private Integer monitoredBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private Integer createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    private Integer updatedBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deletedAt;
    private Integer deletedBy;
}
