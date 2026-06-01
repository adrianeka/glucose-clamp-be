package com.tujuhsembilan.glucoseclamp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionCreateRequest {
    @Schema(description = "ID pasien terkait", example = "PAT-001")
    @NotBlank(message = "Kolom patientId tidak boleh kosong")
    private String patientId;

    @Schema(description = "ID protokol yang digunakan", example = "PR-24H")   
    @NotBlank(message = "Kolom protocolId tidak boleh kosong")
    private String protocolId;

    @Schema(description = "Waktu pengukuran dalam format ISO (YYYY-MM-DD) atau 'YYYY-MM-DD'", example = "2026-05-21")
    @NotNull(message = "Kolom visitDate tidak boleh kosong")
    private LocalDate visitDate;

    @Schema(description = "Waktu mulai sesi dalam format ISO (YYYY-MM-DDTHH:mm:ss) atau 'YYYY-MM-DD HH:mm:ss'", example = "2026-05-21T07:10:00")
    @NotNull(message = "Kolom startTime tidak boleh kosong")
    private LocalDateTime startTime;

    @Schema(description = "Durasi puasa dalam jam", example = "8")
    private Integer fastingHour;

    @Schema(description = "Daftar ID perangkat yang digunakan", example = "[1, 2]")
    @NotEmpty(message = "Kolom deviceIds tidak boleh kosong")
    private List<Integer> deviceIds;

    @Valid
    @NotNull(message = "Kolom vitalSignRequest wajib diisi")
    private VitalSignRequest vitalSignRequest;

    @Valid
    @NotNull(message = "Kolom anamnesisRequest wajib diisi")
    private AnamnesisRequest anamnesisRequest;

    @Valid
    @NotNull(message = "Kolom anthropometryRequest wajib diisi")
    private AnthropometryRequest anthropometryRequest;
}