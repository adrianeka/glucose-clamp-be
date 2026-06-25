package com.tujuhsembilan.glucoseclamp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalConfigurationResponse {
    private BigInteger gconfId;
    private String gconfCode;
    private String gconfValue;
    private String gconfTitle;
    private String gconfDescription;
    private String status;
    private String createdAt;
    private Integer createdBy;
    private String updatedAt;
    private Integer updatedBy;
    private String deletedAt;
    private Integer deletedBy;
}
