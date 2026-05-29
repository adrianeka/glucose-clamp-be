package com.tujuhsembilan.glucoseclamp.dto.request;

import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
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
public class AnthropometryStatusUpdateRequest {

    @Schema(description = "Status entitas: ACTIVE atau INACTIVE", example = "INACTIVE")
    @NotNull(message = "status is required")
    private EntityStatus status;
}
