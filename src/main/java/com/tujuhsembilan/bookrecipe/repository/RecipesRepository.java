package com.tujuhsembilan.bookrecipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tujuhsembilan.bookrecipe.model.Recipes;

@Repository
public interface RecipesRepository extends JpaRepository<Recipes, Integer>{
    
}
