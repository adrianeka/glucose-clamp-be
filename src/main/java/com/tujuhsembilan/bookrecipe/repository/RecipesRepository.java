package com.tujuhsembilan.bookrecipe.repository;

import com.tujuhsembilan.bookrecipe.model.Recipes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RecipesRepository extends JpaRepository<Recipes, Integer>, JpaSpecificationExecutor<Recipes>{
	Optional<Recipes> findByRecipeIdAndUsers_UserId(int recipeId, int userId);
}
