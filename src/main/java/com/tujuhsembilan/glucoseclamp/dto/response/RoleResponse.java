package com.tujuhsembilan.glucoseclamp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {
    private Integer roleId;
    private String roleName;
    private String status;
    private String createdAt;
    private Integer createdBy;
    private String updatedAt;
    private Integer updatedBy;
}
