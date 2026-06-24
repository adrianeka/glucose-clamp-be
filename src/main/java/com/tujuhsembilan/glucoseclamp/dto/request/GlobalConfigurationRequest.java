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
public class GlobalConfigurationRequest {

    @Schema(description = "Unique global configuration code", example = "detik_peringatan_sebelum_act")
    @NotBlank(message = "gconf_code is required")
    @Size(max = 100, message = "gconf_code must not exceed 100 characters")
    private String gconfCode;

    @Schema(description = "Value of the global configuration", example = "180")
    @NotBlank(message = "gconf_value is required")
    @Size(max = 255, message = "gconf_value must not exceed 255 characters")
    private String gconfValue;

    @Schema(description = "Title of the global configuration", example = "Detik Peringatan Sebelum ACT")
    @Size(max = 255, message = "gconf_title must not exceed 255 characters")
    private String gconfTitle;

    @Schema(description = "Description of the global configuration", example = "Detik peringatan sebelum ACT")
    @Size(max = 500, message = "gconf_description must not exceed 500 characters")
    private String gconfDescription;
}
