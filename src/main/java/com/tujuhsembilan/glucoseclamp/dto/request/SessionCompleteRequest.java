package com.tujuhsembilan.glucoseclamp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionCompleteRequest {
    @NotNull(message = "Kolom endTime tidak boleh kosong")
    private LocalDateTime endTime;
}
