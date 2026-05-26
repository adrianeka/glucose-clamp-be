package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.RoleAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleAccessRepository extends JpaRepository<RoleAccess, Integer> {
    @Query("SELECT ra FROM RoleAccess ra WHERE ra.deletedAt IS NULL")
    List<RoleAccess> findAllActive();
    @Query("SELECT ra FROM RoleAccess ra WHERE ra.roleAccessId = ?1 AND ra.deletedAt IS NULL")
    Optional<RoleAccess> findByIdAndDeletedAtIsNull(Integer roleAccessId);
    @Query("SELECT ra FROM RoleAccess ra WHERE ra.role.roleId = ?1 AND ra.deletedAt IS NULL")
    List<RoleAccess> findByRoleIdAndDeletedAtIsNull(Integer roleId);
    @Query("SELECT ra FROM RoleAccess ra WHERE ra.role.roleId = ?1 AND ra.accessMenu.menuId = ?2 AND ra.deletedAt IS NULL")
    Optional<RoleAccess> findByRoleIdAndMenuIdAndDeletedAtIsNull(Integer roleId, Integer menuId);
}
