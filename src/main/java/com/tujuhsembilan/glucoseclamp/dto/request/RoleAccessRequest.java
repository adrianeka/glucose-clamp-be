package com.tujuhsembilan.glucoseclamp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleAccessRequest {
    private Integer roleId;
    private Integer menuId;
    private Boolean canView;
    private Boolean canAdd;
    private Boolean canEdit;
    private Boolean canDelete;
}