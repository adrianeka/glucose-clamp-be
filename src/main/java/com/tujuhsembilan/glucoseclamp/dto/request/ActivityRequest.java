package com.tujuhsembilan.glucoseclamp.dto.request;

import com.tujuhsembilan.glucoseclamp.model.base.ActivityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityRequest {
    @Schema(description = "ID session terkait", example = "2")
    @NotNull(message = "Kolom sessionId tidak boleh kosong")
    private Long sessionId;

    @Schema(description = "Waktu activity", example = "2026-05-30T10:52:00")
    @NotNull(message = "Kolom time tidak boleh kosong")
    private LocalDateTime time;

    @Schema(description = "Tipe activity", example = "BLOOD_RAW")
    @NotBlank(message = "Kolom activityType tidak boleh kosong")
    private String activityType;

    @Schema(description = "Deskripsi activity", example = "Pengambilan darah basal")
    @NotBlank(message = "Kolom activityDesc tidak boleh kosong")
    private String activityDesc;

    @Schema(description = "Status workflow activity", example = "INQUEUE")
    private ActivityStatus activityStatus;
}