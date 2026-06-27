package com.tujuhsembilan.glucoseclamp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreparationCheckResponse {
    private VitalSignResponse vitalSign;
    private AnamnesisResponse anamnesis;
    private AnthropometryResponse anthropometry;
}