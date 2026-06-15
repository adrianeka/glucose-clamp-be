package com.tujuhsembilan.glucoseclamp.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionCompleteRequest {
    @NotNull(message = "Kolom endTime tidak boleh kosong")
    private LocalDateTime endTime;

    @NotNull(message = "Kolom endReasonCategory tidak boleh kosong")
    @Schema(description = "Status sesi setelah selesai", example = "Sesuai dengan protokol")
    private String endReasonCategory;

    @NotNull(message = "Kolom endReasonDetail tidak boleh kosong")
    @Schema(description = "Detail status sesi setelah selesai", example = "Sesuai dengan protokol")
    private String endReasonDetail;
}
