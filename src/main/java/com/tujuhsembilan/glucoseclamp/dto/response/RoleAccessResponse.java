package com.tujuhsembilan.glucoseclamp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleAccessResponse {
    private Integer roleAccessId;
    private Integer roleId;
    private String roleName;
    private Boolean canView;
    private Boolean canAdd;
    private Boolean canEdit;
    private Boolean canDelete;
}