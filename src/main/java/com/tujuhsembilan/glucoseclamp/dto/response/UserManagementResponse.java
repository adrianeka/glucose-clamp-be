package com.tujuhsembilan.glucoseclamp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserManagementResponse {
    private Integer userId;
    private Integer roleId;
    private String roleName;
    private String positionName;
    private String name;
    private String username;
    private String email;
    private String status;
    private String createdAt;
    private Integer createdBy;
    private String updatedAt;
    private Integer updatedBy;
    private String deletedAt;
    private Integer deletedBy;
}
