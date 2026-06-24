package com.tujuhsembilan.glucoseclamp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnthropometryRequest {

    @Schema(description = "ID sesi terkait", example = "101")
    @NotNull(message = "session_id is required")
    private Long sessionId;

    @Schema(description = "Waktu pengukuran dalam format ISO (YYYY-MM-DDTHH:mm:ss)", example = "2026-05-21T07:10:00")
    private String measuredAt;

    @Schema(description = "Berat badan dalam kilogram", example = "70.0")
    @NotNull(message = "weight_kg is required")
    private BigDecimal weightKg;

    @Schema(description = "Tinggi badan dalam sentimeter", example = "170.0")
    @NotNull(message = "height_cm is required")
    private BigDecimal heightCm;

    @Schema(description = "Body Mass Index", example = "24.2")
    @NotNull(message = "bmi is required")
    private BigDecimal bmi;

    @Schema(description = "Lingkar pinggang dalam sentimeter", example = "80.0")
    @NotNull(message = "waist_circumference_cm is required")
    private BigDecimal waistCircumferenceCm;

    @Schema(description = "ID pengguna yang menugaskan", example = "1")
    private Integer assignedBy;
}
