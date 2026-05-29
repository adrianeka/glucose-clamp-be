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
public class VitalSignUpdateRequest {
    @Schema(description = "Waktu pengukuran dalam format ISO (YYYY-MM-DDTHH:mm:ss) atau 'YYYY-MM-DD HH:mm:ss'", example = "2026-05-21T07:10:00")
    private String measuredAt;

    @Schema(description = "Tekanan darah sistolik (mmHg)", example = "110")
    private Integer systolic;

    @Schema(description = "Tekanan darah diastolik (mmHg)", example = "70")
    private Integer diastolic;

    @Schema(description = "Denyut nadi (bpm)", example = "80")
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
