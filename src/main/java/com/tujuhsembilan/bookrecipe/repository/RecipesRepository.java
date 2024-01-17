package com.tujuhsembilan.bookrecipe.repository;

import com.tujuhsembilan.bookrecipe.model.Recipes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipesRepository extends JpaRepository<Recipes, Integer>, JpaSpecificationExecutor<Recipes>{
	@Query("SELECT r FROM Recipes r WHERE r.recipeId = :recipeId AND r.users.userId = :userId")
	Optional<Recipes> findByMyRecipe(@Param("recipeId") int recipeId, @Param("userId") int userId);
}
