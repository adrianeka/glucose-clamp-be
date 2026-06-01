package com.tujuhsembilan.glucoseclamp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionActivityCompleteRequest {
    @NotNull(message = "Kolom performedAt tidak boleh kosong")
    private LocalDateTime performedAt;

    private String notes;
}