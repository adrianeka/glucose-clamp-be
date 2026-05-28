package com.tujuhsembilan.glucoseclamp.dto.request;

import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceStatusUpdateRequest {

    @Schema(description = "Status perangkat (ACTIVE atau INACTIVE)", example = "INACTIVE")
    @NotNull(message = "Status tidak boleh kosong")
    private EntityStatus status;
}
