package com.tujuhsembilan.bookrecipe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tujuhsembilan.bookrecipe.model.Users;

public interface UsersRepository extends JpaRepository<Users, Integer>{
    Optional<Users> findByUsername(String username);
    Boolean existsByUsername(String username);
}
