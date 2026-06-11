package com.tujuhsembilan.glucoseclamp.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhaseConfigurationResponse {

    private Long phaseConfId;
    private Integer phaseConfPriority;
    private String phaseConfCode;
    private String phaseConfName;
    private String phaseConfType;
    private String status;
    private LocalDateTime createdAt;
    private Integer createdBy;
    private LocalDateTime updatedAt;
    private Integer updatedBy;
    private LocalDateTime deletedAt;
    private Integer deletedBy;
}
