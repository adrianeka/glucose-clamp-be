package com.tujuhsembilan.bookrecipe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import com.tujuhsembilan.bookrecipe.dto.request.RecipeFilterRequestDTO;
import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;
import com.tujuhsembilan.bookrecipe.model.FavoriteFoodsId;
import com.tujuhsembilan.bookrecipe.model.Recipes;
import com.tujuhsembilan.bookrecipe.model.Users;
import com.tujuhsembilan.bookrecipe.repository.FavoriteFoodsRepository;
import com.tujuhsembilan.bookrecipe.repository.RecipeListRepository;
import com.tujuhsembilan.bookrecipe.repository.RecipesRepository;
import com.tujuhsembilan.bookrecipe.repository.UsersRepository;
import com.tujuhsembilan.bookrecipe.service.method.RecipeFilterMethod;

import java.util.HashMap;
import java.util.Map;

@Service
public class RecipeListService {
    @Autowired
    private RecipesRepository recipeRepo;

    @Autowired
    private RecipeListRepository recipeListRepo;

    @Autowired
    private FavoriteFoodsRepository favoriteFoodsRepo;

    @Autowired
    private UsersRepository userRepo;

    public ResponseEntity<Object> getAllRecipes(int pageSize, int pageNumber, RecipeFilterRequestDTO recipeFiltersDTO) {
        Map<String, Object> result = new HashMap<>();
        HttpStatus status = HttpStatus.CREATED;
        String message = "";
        RecipeFilterMethod recipeFilterMethod = new RecipeFilterMethod();

        try {

            recipeFilterMethod.filterRecipe(recipeListRepo, result, status, pageSize, pageNumber, recipeFiltersDTO);

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Failed to Get Data!";

            result.put("error", e.getMessage());
            result.put("message", message);
            result.put("status", status);

        }
        return ResponseEntity.status(status).body(result);
    }

    public ResponseEntity<Object> toggleFavorite(int recipeId) {
        Map<String, Object> result = new HashMap<>();
        HttpStatus status = HttpStatus.CREATED;
        String message = "Resep " + recipeId + " berhasil ditambahkan ke dalam favorit";
        // User Dummy
        int userId = 9;

        try {
            Recipes recipesData = recipeRepo.findById(recipeId).orElse(null);

            if (recipesData == null) {
                status = HttpStatus.NOT_FOUND;
                message = "Data dengan id " + recipeId + " tidak ditemukan!";
            } else {
                String recipeName = recipesData.getRecipeName();
                FavoriteFoods favoriteFoods = favoriteFoodsRepo
                                                .findById_RecipeIdAndId_UserId(userId, recipesData.getRecipeId())
                                                .orElse(null);
                
                FavoriteFoodsId favId = new FavoriteFoodsId();

                if (favoriteFoods == null) {
                    Users users = userRepo.findById(userId).orElse(null);
                    FavoriteFoods favorite = new FavoriteFoods();
                    favId.setIsFavorite(true);
                    favId.setUserId(users.getUserId());
                    favId.setRecipeId(recipesData.getRecipeId());

                    favorite.setUsers(users);
                    favorite.setRecipes(recipesData);
                    favorite.setId(favId);
                    favorite = favoriteFoodsRepo.save(favorite);
                    message = "Resep " + recipeName + " berhasil ditambahkan ke favorite!";
                } else {
                    favId.setUserId(userId);
                    favId.setRecipeId(recipesData.getRecipeId());
                    boolean isFavorite = favoriteFoods.getId().getIsFavorite();
                    if (isFavorite) {
                        favId.setIsFavorite(false);
                        favoriteFoods.setId(favId);
                        message = "Resep " + recipeName + " berhasil dihapus dari favorite!";
                    } else {
                        favId.setIsFavorite(true);
                        favoriteFoods.setId(favId);
                        message = "Resep " + recipeName + " berhasil ditambahkan ke favorite!";
                    }
                    favoriteFoods = favoriteFoodsRepo.save(favoriteFoods);
                }
            }

            result.put("total", recipeRepo.count());
            result.put("data", null);
            result.put("message", message);
            result.put("status", status);
            return ResponseEntity.status(status).body(result);

        } catch (Exception e) {
            message = "Error Exception";
            status = HttpStatus.INTERNAL_SERVER_ERROR;

            result.put("message", message);
            result.put("status", status);
            return ResponseEntity.status(status).body(result);
        }
    }
}
