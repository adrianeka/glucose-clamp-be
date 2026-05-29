package com.tujuhsembilan.glucoseclamp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnamnesisUpdateRequest {
    @Schema(description = "Keluhan utama", example = "Sakit kepala ringan")
    private String chiefComplaint;

    @Schema(description = "Riwayat medis", example = "Hipertensi terkontrol")
    private String medicalHistory;

    @Schema(description = "ID pengguna yang menugaskan", example = "2")
    private Integer assignedBy;
}
