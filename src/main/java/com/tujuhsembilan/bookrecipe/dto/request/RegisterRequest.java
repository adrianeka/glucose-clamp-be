package com.tujuhsembilan.bookrecipe.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Kolom username tidak boleh kosong")
    @Size(max = 100, message = "Format username belum sesuai.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Format username belum sesuai.")
    private String username;

    @NotBlank(message = "Kolom nama lengkap tidak boleh kosong")
    @Size(max = 255, message = "Format nama lengkap belum sesuai. (Tidak menggunakan special character dan maksimal 255 charackter)")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Format nama lengkap belum sesuai. (Tidak menggunakan special character dan maksimal 255 charackter)")
    private String fullname;

    @NotBlank(message = "Kolom kata sandi tidak boleh kosong")
    @Size(max = 50, min = 6, message = "Kata sandi tidak boleh kurang dari 6 karakter")
    private String password;

    @NotBlank(message = "Kolom konfirmasi kata sandi tidak boleh kosong")
    @Size(max = 50, min = 6, message = "Kata sandi tidak boleh kurang dari 6 karakter")
    private String retypePassword;
}
