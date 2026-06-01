package com.tujuhsembilan.glucoseclamp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionLabResultRequest {
    private String bloodSampleId;

    @NotBlank(message = "Kolom parameterName tidak boleh kosong")
    private String parameterName;

    @NotNull(message = "Kolom value tidak boleh kosong")
    private BigDecimal value;

    @NotNull(message = "Kolom referenceRangeMin tidak boleh kosong")
    private BigDecimal referenceRangeMin;

    @NotNull(message = "Kolom referenceRangeMax tidak boleh kosong")
    private BigDecimal referenceRangeMax;

    @NotBlank(message = "Kolom unit tidak boleh kosong")
    private String unit;

    @NotBlank(message = "Kolom abnormalFlag tidak boleh kosong")
    private String abnormalFlag;
}