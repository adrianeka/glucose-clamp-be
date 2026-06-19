package com.tujuhsembilan.glucoseclamp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhaseConfigurationBulkPriorityRequest {

    @NotEmpty(message = "Daftar prioritas tidak boleh kosong")
    @Valid
    private List<PhaseConfigurationPriorityItemRequest> priorities;
}