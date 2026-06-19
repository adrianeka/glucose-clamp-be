package com.tujuhsembilan.glucoseclamp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionUpdateRequest {

    @Schema(description = "ID protokol yang digunakan", example = "PR-24H")  
    @NotBlank(message = "Protocol ID wajib diisi")
    private Long protocolId;

    @Schema(description = "Waktu pengukuran dalam format ISO (YYYY-MM-DD) atau 'YYYY-MM-DD'", example = "2026-05-22")
    @NotNull(message = "Visit date wajib diisi")
    private LocalDate visitDate;

    @Schema(description = "Waktu mulai sesi dalam format ISO (YYYY-MM-DDTHH:mm:ss) atau 'YYYY-MM-DD HH:mm:ss'", example = "2026-05-21T09:10:00")
    @NotNull(message = "Start time wajib diisi")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer fastingHour;
}
