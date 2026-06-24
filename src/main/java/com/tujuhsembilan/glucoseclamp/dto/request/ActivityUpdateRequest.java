package com.tujuhsembilan.glucoseclamp.dto.request;

import com.tujuhsembilan.glucoseclamp.model.base.ActivityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityUpdateRequest {
    @Schema(description = "ID session terkait", example = "2")
    private Long sessionId;

    @Schema(description = "Waktu activity", example = "2026-05-30T10:52:00")
    private String startTime;

    @Schema(description = "Tipe activity", example = "BLOOD_RAW")
    private String activityType;

    @Schema(description = "Deskripsi activity", example = "Pengambilan darah basal")
    private String activityDesc;

    @Schema(description = "Phase Name", example = "Pengambilan darah basal")
    private String phaseName;

    @Schema(description = "Phase Code", example = "Pengambilan darah basal")
    private String phaseCode;

    @Schema(description = "Status workflow activity", example = "INQUEUE")
    private ActivityStatus activityStatus;
}