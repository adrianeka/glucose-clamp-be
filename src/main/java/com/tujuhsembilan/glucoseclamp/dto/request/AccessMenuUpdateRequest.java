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
public class AccessMenuUpdateRequest {

    @Schema(description = "Nama menu baru", example = "Dashboard Klinis")
    @NotBlank(message = "Nama menu tidak boleh kosong")
    @Size(max = 100, message = "Nama menu maksimal 100 karakter")
    private String menuName;
}
