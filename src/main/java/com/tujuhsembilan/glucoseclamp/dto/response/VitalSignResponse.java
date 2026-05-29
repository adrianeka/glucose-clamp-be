package com.tujuhsembilan.glucoseclamp.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class VitalSignResponse {
    private Integer vitalId;
    private Integer sessionId;
    private LocalDateTime measuredAt;
    private Integer systolic;
    private Integer diastolic;
    private Integer pulse;
    private Integer respiratoryRate;
    private BigDecimal temperatureC;
    private BigDecimal spo2;
    private Integer assignedBy;
    private LocalDateTime createdAt;
    private Integer createdBy;
    private LocalDateTime updatedAt;
    private Integer updatedBy;
    private LocalDateTime deletedAt;
    private Integer deletedBy;
    private EntityStatus status;
}
