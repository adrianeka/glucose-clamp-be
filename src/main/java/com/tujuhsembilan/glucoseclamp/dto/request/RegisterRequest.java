package com.tujuhsembilan.glucoseclamp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body untuk pendaftaran pengguna baru")
public class RegisterRequest {

    @NotBlank(message = "Kolom username tidak boleh kosong")
    @Size(max = 100, message = "Format username belum sesuai.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Format username belum sesuai.")
    @Schema(description = "Username unik untuk masuk ke sistem (hanya huruf dan angka)", example = "johndoe123")
    private String username;

    @NotBlank(message = "Kolom nama lengkap tidak boleh kosong")
    @Size(max = 255, message = "Format nama lengkap belum sesuai. (Tidak menggunakan special character dan maksimal 255 charackter)")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Format nama lengkap belum sesuai. (Tidak menggunakan special character dan maksimal 255 charackter)")
    @Schema(description = "Nama lengkap pengguna tanpa karakter spesial", example = "John Doe")
    private String fullname;

    @NotBlank(message = "Kolom email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    @Size(max = 150, message = "Email maksimal 150 karakter")
    @Schema(description = "Alamat email aktif pengguna", example = "johndoe@example.com")
    private String email; // Menambahkan properti email

    @NotBlank(message = "Kolom jabatan tidak boleh kosong")
    @Size(max = 150, message = "Nama jabatan maksimal 150 karakter")
    @Schema(description = "Nama jabatan atau posisi kerja pengguna", example = "Operator Analyzer")
    private String positionName;

   @NotBlank(message = "Kolom kata sandi tidak boleh kosong")
    @Size(
        max = 50,
        min = 6,
        message = "Kata sandi tidak boleh kurang dari 6 karakter"
    )
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z]).+$",
        message = "Kata sandi harus mengandung minimal 1 huruf besar dan 1 huruf kecil"
    )
    @Schema(
        description = "Kata sandi akun minimal 6 karakter dan harus mengandung huruf besar dan kecil",
        example = "Password123"
    )
    private String password;

    @NotBlank(message = "Kolom konfirmasi kata sandi tidak boleh kosong")
    @Size(
        max = 50,
        min = 6,
        message = "Kata sandi tidak boleh kurang dari 6 karakter"
    )
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z]).+$",
        message = "Konfirmasi kata sandi harus mengandung minimal 1 huruf besar dan 1 huruf kecil"
    )
    @Schema(
        description = "Konfirmasi kata sandi harus sama dengan kolom password",
        example = "Password123"
    )
    private String retypePassword;

    @NotNull(message = "Role wajib diisi")
    @Schema(description = "ID Role yang diberikan kepada pengguna", example = "1")
    private Integer roleId;
}