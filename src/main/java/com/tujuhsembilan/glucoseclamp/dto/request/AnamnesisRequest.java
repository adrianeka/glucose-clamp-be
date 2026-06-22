package com.tujuhsembilan.glucoseclamp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnamnesisRequest {

    @Schema(description = "ID sesi terkait", example = "101")
    @NotNull(message = "session_id is required")
    private Long sessionId;

    @Schema(description = "Tanggal anamnesis (YYYY-MM-DD)", example = "2026-05-21")
    @NotNull(message = "date is required")
    private String date;

    @Schema(description = "Keluhan utama", example = "Tidak ada keluhan (Sehat)")
    @NotNull(message = "chief_complaint is required")
    private String chiefComplaint;

    @Schema(description = "Riwayat medis", example = "Tidak ada")
    private String medicalHistory;

    @Schema(description = "ID pengguna yang menugaskan", example = "1")
    private Integer assignedBy;
}
