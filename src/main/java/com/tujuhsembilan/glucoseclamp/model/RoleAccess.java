package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "role_access")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"role", "accessMenu"}, callSuper = true)
@ToString(exclude = {"role", "accessMenu"})
@Builder
public class RoleAccess extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_access_id_seq")
    @SequenceGenerator(name = "role_access_id_seq", sequenceName = "role_access_id_seq", allocationSize = 1)
    @Column(name = "role_access_id")
    private Integer roleAccessId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "access_menu_id", nullable = false)
    private AccessMenu accessMenu;
    
    @Column(name = "can_view")
    private Boolean canView = false;
    
    @Column(name = "can_add")
    private Boolean canAdd = false;
    
    @Column(name = "can_edit")
    private Boolean canEdit = false;
    
    @Column(name = "can_delete")
    private Boolean canDelete = false;
}
