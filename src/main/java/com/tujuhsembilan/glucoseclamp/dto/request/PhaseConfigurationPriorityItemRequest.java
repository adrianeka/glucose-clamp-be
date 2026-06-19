package com.tujuhsembilan.glucoseclamp.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhaseConfigurationPriorityItemRequest {

    @NotNull(message = "ID tidak boleh kosong")
    private Long id;

    @NotNull(message = "Priority tidak boleh kosong")
    @Min(value = 1, message = "Priority harus lebih besar dari 0")
    private Integer priority;
}