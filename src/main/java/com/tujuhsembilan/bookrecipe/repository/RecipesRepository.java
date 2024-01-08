package com.tujuhsembilan.bookrecipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.tujuhsembilan.bookrecipe.model.Recipes;

public interface RecipesRepository extends JpaRepository<Recipes, Integer>, JpaSpecificationExecutor<Recipes>{
}
