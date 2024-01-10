package com.tujuhsembilan.bookrecipe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.tujuhsembilan.bookrecipe.model.Recipes;

public interface RecipesRepository extends JpaRepository<Recipes, Integer>, JpaSpecificationExecutor<Recipes>{
	Optional<Recipes> findByRecipeIdAndUsers_UserId(int recipeId, int userId);
	Long countByUsers_UserId(int userId);
}
