package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
    List<User> findAllActive();
    @Query("SELECT u FROM User u WHERE u.userId = ?1 AND u.deletedAt IS NULL")
    Optional<User> findByIdAndDeletedAtIsNull(Integer userId);
    @Query("SELECT u FROM User u WHERE u.username = ?1 AND u.deletedAt IS NULL")
    Optional<User> findByUsernameAndDeletedAtIsNull(String username);
    @Query("SELECT u FROM User u WHERE u.email = ?1 AND u.deletedAt IS NULL")
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    @Query("SELECT u FROM User u WHERE u.role.roleId = ?1 AND u.deletedAt IS NULL")
    List<User> findByRoleIdAndDeletedAtIsNull(Integer roleId);
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = ?1 AND u.deletedAt IS NULL")
    boolean existsByUsername(String username);
}
