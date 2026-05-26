package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query("SELECT r FROM Role r WHERE r.deletedAt IS NULL")
    List<Role> findAllActive();
    @Query("SELECT r FROM Role r WHERE r.roleId = ?1 AND r.deletedAt IS NULL")
    Optional<Role> findByIdAndDeletedAtIsNull(Integer roleId);
    @Query("SELECT r FROM Role r WHERE r.roleName = ?1 AND r.deletedAt IS NULL")
    Optional<Role> findByRoleNameAndDeletedAtIsNull(String roleName);
}
