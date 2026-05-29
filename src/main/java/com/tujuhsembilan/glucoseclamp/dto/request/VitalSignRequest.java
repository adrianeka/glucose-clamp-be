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
public class VitalSignRequest {

    @Schema(description = "ID sesi terkait", example = "101")
    @NotNull(message = "session_id is required")
    private Integer sessionId;

    @Schema(description = "Waktu pengukuran dalam format ISO (YYYY-MM-DDTHH:mm:ss) atau 'YYYY-MM-DD HH:mm:ss'", example = "2026-05-21T07:10:00")
    @NotNull(message = "measured_at is required")
    private String measuredAt;

    @Schema(description = "Tekanan darah sistolik (mmHg)", example = "110")
    @NotNull(message = "systolic is required")
    private Integer systolic;

    @Schema(description = "Tekanan darah diastolik (mmHg)", example = "70")
    @NotNull(message = "diastolic is required")
    private Integer diastolic;

    @Schema(description = "Denyut nadi (bpm)", example = "80")
    @NotNull(message = "pulse is required")
    private Integer pulse;

    @Schema(description = "Laju pernapasan (rpm)", example = "18")
    private Integer respiratoryRate;

    @Schema(description = "Suhu tubuh dalam Celcius", example = "36.7")
    private BigDecimal temperatureC;

    @Schema(description = "Saturasi oksigen (%)", example = "99")
    private BigDecimal spo2;

    @Schema(description = "ID pengguna yang melakukan pengukuran", example = "1")
    private Integer assignedBy;
}
