package com.tujuhsembilan.glucoseclamp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateRequest {

    @Schema(description = "Nama role baru", example = "Supervisor Utama")
    @NotBlank(message = "Nama role tidak boleh kosong")
    @Size(max = 100, message = "Nama role maksimal 100 karakter")
    private String roleName;
}
