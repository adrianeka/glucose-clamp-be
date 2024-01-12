package com.tujuhsembilan.bookrecipe.repository;

import com.tujuhsembilan.bookrecipe.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUsername(String username);

    Boolean existsByUsername(String username);
}
