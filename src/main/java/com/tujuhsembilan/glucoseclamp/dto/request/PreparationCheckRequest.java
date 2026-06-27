package com.tujuhsembilan.glucoseclamp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreparationCheckRequest {
    private Long activityId;
    private VitalSignRequest vitalSign;
    private AnamnesisRequest anamnesis;
    private AnthropometryRequest anthropometry;
    
}
