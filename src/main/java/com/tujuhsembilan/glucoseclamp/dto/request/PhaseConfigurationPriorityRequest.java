package com.tujuhsembilan.glucoseclamp.dto.request;

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
public class PhaseConfigurationPriorityRequest {

    @Schema(description = "New priority number", example = "1")
    @NotNull(message = "phase_conf_priority is required")
    private Integer phaseConfPriority;
}
