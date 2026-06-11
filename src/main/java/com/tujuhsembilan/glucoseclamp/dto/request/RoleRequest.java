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
public class RoleRequest {

    @Schema(description = "Nama role", example = "Admin")
    @NotBlank(message = "Nama role tidak boleh kosong")
    @Size(max = 100, message = "Nama role maksimal 100 karakter")
    private String roleName;
}
