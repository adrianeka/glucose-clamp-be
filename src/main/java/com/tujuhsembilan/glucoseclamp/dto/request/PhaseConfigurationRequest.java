package com.tujuhsembilan.glucoseclamp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhaseConfigurationRequest {

    // @Schema(description = "Priority of the phase configuration", example = "1")
    // @NotNull(message = "phase_conf_priority is required")
    // private Integer phaseConfPriority;

    @Schema(description = "Unique phase configuration code", example = "PREP1")
    @NotBlank(message = "phase_conf_code is required")
    @Size(max = 100, message = "phase_conf_code must not exceed 100 characters")
    private String phaseConfCode;

    @Schema(description = "Display name of the phase configuration", example = "Pemeriksaan Awal")
    @NotBlank(message = "phase_conf_name is required")
    @Size(max = 255, message = "phase_conf_name must not exceed 255 characters")
    private String phaseConfName;

    @Schema(description = "Type of the phase configuration", example = "preparation")
    @NotBlank(message = "phase_conf_type is required")
    @Size(max = 100, message = "phase_conf_type must not exceed 100 characters")
    private String phaseConfType;
}
