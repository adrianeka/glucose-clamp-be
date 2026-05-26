package com.tujuhsembilan.glucoseclamp.dto.request;

import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a patient's status (ACTIVE / INACTIVE).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientStatusUpdateRequest {

    @Schema(description = "Status pasien (ACTIVE atau INACTIVE)", example = "ACTIVE")
    @NotNull(message = "Status tidak boleh kosong")
    private EntityStatus status;
}
