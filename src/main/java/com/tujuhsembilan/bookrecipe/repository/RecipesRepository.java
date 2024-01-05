package com.tujuhsembilan.bookrecipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tujuhsembilan.bookrecipe.model.Recipes;

public interface RecipesRepository extends JpaRepository<Recipes, Integer>{
    
}
