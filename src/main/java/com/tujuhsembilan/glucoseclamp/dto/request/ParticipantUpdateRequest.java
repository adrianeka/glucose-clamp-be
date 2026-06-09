package com.tujuhsembilan.glucoseclamp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantUpdateRequest {

    @Schema(description = "Nomor rekam medis unik milik pasien", example = "MR889100")
    @Size(max = 20, message = "Nomor rekam medis maksimal 20 karakter")
    private String medicalRecordNo;

    @Schema(description = "Nama lengkap pasien", example = "John Doe")
    @Size(max = 255, message = "Nama maksimal 255 karakter")
    private String name;

    @Schema(description = "Jenis kelamin pasien", example = "Male")
    @Pattern(regexp = "^(Male|Female)$", message = "Gender harus Male atau Female")
    private String gender;

    @Schema(description = "Tanggal lahir pasien", example = "1990-01-01")
    private String dob;

    @Schema(description = "Usia pasien", example = "25")
    @NotNull(message = "Usia tidak boleh kosong")
    @Min(value = 0, message = "Usia harus positif")
    @Max(value = 150, message = "Usia tidak boleh melebihi 150 tahun")
    private Integer age;

    @Schema(description = "Nomor handphone pasien", example = "081234567890")
    @Size(max = 20, message = "Nomor telepon maksimal 20 karakter")
    private String numberPhone;

}
