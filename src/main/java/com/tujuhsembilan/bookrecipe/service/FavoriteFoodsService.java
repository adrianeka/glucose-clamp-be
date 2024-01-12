package com.tujuhsembilan.bookrecipe.service;

import com.tujuhsembilan.bookrecipe.repository.FavoriteFoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FavoriteFoodsService {
    @Autowired
    private FavoriteFoodsRepository favoriteFoodsRepository;
    public Optional<Boolean> findIsFavoriteByUserIdAndRecipeId(int userId, int recipeId) {
        return favoriteFoodsRepository.findIsFavorite(userId, recipeId);
    }
}
