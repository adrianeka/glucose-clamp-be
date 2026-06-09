package com.tujuhsembilan.glucoseclamp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantRequest {

    @Schema(description = "Nomor rekam medis unik milik pasien", example = "MR889100")
    @NotBlank(message = "Nomor rekam medis tidak boleh kosong")
    @Size(max = 20, message = "Nomor rekam medis maksimal 20 karakter")
    private String medicalRecordNo;

    @Schema(description = "Nama lengkap pasien", example = "John Doe")
    @NotBlank(message = "Nama tidak boleh kosong")
    @Size(max = 255, message = "Nama maksimal 255 karakter")
    private String name;

    @Schema(description = "Jenis kelamin pasien (Male / Female)", example = "Male")
    @NotBlank(message = "Gender tidak boleh kosong")
    @Pattern(regexp = "^(Male|Female)$", message = "Gender harus Male atau Female")
    private String gender;

    @Schema(description = "Tanggal lahir dengan format YYYY-MM-DD", example = "1998-06-10")
    @NotBlank(message = "Tanggal lahir tidak boleh kosong")
    private String dob;

    @Schema(description = "Usia pasien", example = "25")
    @NotNull(message = "Usia tidak boleh kosong")
    @Min(value = 0, message = "Usia harus positif")
    @Max(value = 150, message = "Usia tidak boleh melebihi 150 tahun")
    private Integer age;

    @Schema(description = "Nomor handphone yang dapat dihubungi", example = "081234567890")
    @NotBlank(message = "Nomor telepon tidak boleh kosong")
    @Size(max = 15, message = "Nomor telepon maksimal 15 karakter")
    private String numberPhone;

}
