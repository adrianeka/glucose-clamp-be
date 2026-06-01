package com.tujuhsembilan.glucoseclamp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionBloodSampleRequest {
    @NotNull(message = "Kolom sampleTime tidak boleh kosong")
    private LocalDateTime sampleTime;

    @NotBlank(message = "Kolom sampleType tidak boleh kosong")
    private String sampleType;

    @NotBlank(message = "Kolom tubeType tidak boleh kosong")
    private String tubeType;

    @NotNull(message = "Kolom volumeMl tidak boleh kosong")
    private BigDecimal volumeMl;
}