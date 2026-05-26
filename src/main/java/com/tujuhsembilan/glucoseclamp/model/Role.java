package com.tujuhsembilan.glucoseclamp.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "users", callSuper = true)
@ToString(exclude = "users")
@Builder
public class Role extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_seq")
    @SequenceGenerator(name = "role_id_seq", sequenceName = "role_id_seq", allocationSize = 1)
    @Column(name = "role_id")
    private Integer roleId;
    
    @Column(name = "role_name", nullable = false, length = 100)
    private String roleName;
    
    @OneToMany(mappedBy = "role")
    private List<User> users;
    
    @OneToMany(mappedBy = "role")
    private List<RoleAccess> roleAccesses;
}