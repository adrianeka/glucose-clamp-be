package com.tujuhsembilan.bookrecipe.repository;

import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;
import com.tujuhsembilan.bookrecipe.model.FavoriteFoodsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FavoriteFoodsRepository extends JpaRepository<FavoriteFoods, FavoriteFoodsId>,
        JpaSpecificationExecutor<FavoriteFoods> {
    Optional<FavoriteFoods> findById_RecipeIdAndId_UserId(Integer recipeId, Integer userId);

    @Query(value = "SELECT f.id.isFavorite FROM FavoriteFoods f WHERE f.users.userId = :userId and f.recipes.recipeId = :recipeId")
    Optional<Boolean> findIsFavoriteByUserIdAndRecipeId(@Param("userId") Integer userId, @Param("recipeId") Integer recipeId);
}
