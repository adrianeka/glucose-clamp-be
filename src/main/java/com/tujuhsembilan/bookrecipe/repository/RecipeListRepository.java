package com.tujuhsembilan.bookrecipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.tujuhsembilan.bookrecipe.model.Recipes;

@Repository
@EnableJpaRepositories
public interface RecipeListRepository extends JpaRepository<Recipes, Integer>, JpaSpecificationExecutor<Recipes> {
    Recipes findByRecipeId(int recipeId);
}