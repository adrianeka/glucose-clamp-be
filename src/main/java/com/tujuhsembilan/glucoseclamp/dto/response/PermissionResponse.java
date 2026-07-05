package com.tujuhsembilan.glucoseclamp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponse {
    private Integer menuId;
    private String menuName;
    private Boolean canView;
    private Boolean canAdd;
    private Boolean canEdit;
    private Boolean canDelete;
}