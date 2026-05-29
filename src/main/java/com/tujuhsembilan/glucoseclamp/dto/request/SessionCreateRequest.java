package com.tujuhsembilan.glucoseclamp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionCreateRequest {
    @NotBlank(message = "Kolom patientId tidak boleh kosong")
    private String patientId;

    @NotBlank(message = "Kolom protocolId tidak boleh kosong")
    private String protocolId;

    @NotNull(message = "Kolom visitDate tidak boleh kosong")
    private LocalDate visitDate;

    @NotNull(message = "Kolom startTime tidak boleh kosong")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer fastingHour;
}