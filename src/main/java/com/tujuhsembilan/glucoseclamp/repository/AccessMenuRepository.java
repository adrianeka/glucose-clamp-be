package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.AccessMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccessMenuRepository extends JpaRepository<AccessMenu, Integer> {
    @Query("SELECT m FROM AccessMenu m WHERE m.deletedAt IS NULL")
    List<AccessMenu> findAllActive();
    @Query("SELECT m FROM AccessMenu m WHERE m.menuId = ?1 AND m.deletedAt IS NULL")
    Optional<AccessMenu> findByIdAndDeletedAtIsNull(Integer menuId);
    @Query("SELECT m FROM AccessMenu m WHERE m.menuName = ?1 AND m.deletedAt IS NULL")
    Optional<AccessMenu> findByMenuNameAndDeletedAtIsNull(String menuName);
}
