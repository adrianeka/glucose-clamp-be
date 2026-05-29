package com.tujuhsembilan.glucoseclamp.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnamnesisResponse {
    private Integer anamnesisId;
    private Integer sessionId;
    private LocalDate date;
    private String chiefComplaint;
    private String medicalHistory;
    private Integer assignedBy;
    private LocalDateTime createdAt;
    private Integer createdBy;
    private LocalDateTime updatedAt;
    private Integer updatedBy;
    private LocalDateTime deletedAt;
    private Integer deletedBy;
    private EntityStatus status;
}
