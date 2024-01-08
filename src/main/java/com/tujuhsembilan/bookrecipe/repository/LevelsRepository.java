package com.tujuhsembilan.bookrecipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tujuhsembilan.bookrecipe.model.Levels;

@Repository
public interface LevelsRepository extends JpaRepository<Levels, Integer>{
    
}
