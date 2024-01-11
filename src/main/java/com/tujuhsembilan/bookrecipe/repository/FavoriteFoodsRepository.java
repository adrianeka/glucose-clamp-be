package com.tujuhsembilan.bookrecipe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;
import com.tujuhsembilan.bookrecipe.model.FavoriteFoodsId;

public interface FavoriteFoodsRepository extends JpaRepository<FavoriteFoods, FavoriteFoodsId>{
    Optional<FavoriteFoods> findById_RecipeIdAndId_UserId(Integer recipeId, Integer userId);
}
