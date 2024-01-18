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
    @Query("SELECT f.isFavorite FROM FavoriteFoods f WHERE f.id.userId = :userId and f.id.recipeId = :recipeId")
    Optional<Boolean> findIsFavorite(@Param("userId") int userId, @Param("recipeId") int recipeId);

    @Query("SELECT f FROM FavoriteFoods f WHERE f.id.recipeId = :recipeId and f.id.userId = :userId")
    Optional<FavoriteFoods> findMyFavorite(@Param("recipeId") int recipeId, @Param("userId") int userId);
}
