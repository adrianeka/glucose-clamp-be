package com.tujuhsembilan.glucoseclamp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnthropometryUpdateRequest {

    @Schema(description = "Waktu pengukuran dalam format ISO (YYYY-MM-DDTHH:mm:ss)")
    private String measuredAt;

    @Schema(description = "Berat badan dalam kilogram", example = "70.0")
    private BigDecimal weightKg;

    @Schema(description = "Tinggi badan dalam sentimeter", example = "170.0")
    private BigDecimal heightCm;

    @Schema(description = "Body Mass Index", example = "24.2")
    private BigDecimal bmi;

    @Schema(description = "Lingkar pinggang dalam sentimeter", example = "80.0")
    private BigDecimal waistCircumferenceCm;

    @Schema(description = "ID pengguna yang menugaskan", example = "1")
    private Integer assignedBy;
}
