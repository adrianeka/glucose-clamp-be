package com.tujuhsembilan.glucoseclamp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserManagementRequestEdit {

    @Schema(description = "Role ID", example = "2")
    @NotNull(message = "role_id is required")
    private Integer roleId;

    @Schema(description = "Position or job title", example = "Administrasi")
    @NotBlank(message = "position_name is required")
    @Size(max = 100, message = "position_name must not exceed 100 characters")
    private String positionName;

    @Schema(description = "Full name of user", example = "Admin RSCM")
    @NotBlank(message = "name is required")
    @Size(max = 150, message = "name must not exceed 150 characters")
    private String name;

    @Schema(description = "Username for login", example = "admin")
    @NotBlank(message = "username is required")
    @Size(max = 100, message = "username must not exceed 100 characters")
    private String username;

    @Schema(description = "User email address", example = "admin@mail.com")
    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    @Size(max = 150, message = "email must not exceed 150 characters")
    private String email;

    @Schema(
        description = "Optional password. Leave empty if password is not changed.",
        example = "Password123",
        nullable = true
    )
    @Pattern(
        regexp = "^(|(?=.*[a-z])(?=.*[A-Z]).{6,})$",
        message = "password must be at least 6 characters and contain uppercase and lowercase letters"
    )
    private String password;
}