package com.tujuhsembilan.glucoseclamp.dto.request;

import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfusionMonitoringStatusUpdateRequest {
    @Schema(description = "Updated status of the infusion monitoring", example = "ACTIVE")
    @NotNull(message = "status is required")
    private EntityStatus status;
}
