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
public class SessionUpdateRequest {
    @NotBlank(message = "Protocol ID wajib diisi")
    private String protocolId;

    @NotNull(message = "Visit date wajib diisi")
    private LocalDate visitDate;

    @NotNull(message = "Start time wajib diisi")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer fastingHour;
}
