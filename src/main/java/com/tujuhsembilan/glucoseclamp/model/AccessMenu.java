package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "access_menus")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "roleAccesses", callSuper = true)
@ToString(exclude = "roleAccesses")
@Builder
public class AccessMenu extends BaseEntity {
    
    @Id
    @Column(name = "menu_id")
    private Integer menuId;
    
    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;
    
    @OneToMany(mappedBy = "accessMenu")
    private List<RoleAccess> roleAccesses;
}
