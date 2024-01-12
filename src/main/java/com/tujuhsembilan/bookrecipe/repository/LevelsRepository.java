package com.tujuhsembilan.bookrecipe.repository;

import com.tujuhsembilan.bookrecipe.model.Levels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelsRepository extends JpaRepository<Levels, Integer>{
    
}
