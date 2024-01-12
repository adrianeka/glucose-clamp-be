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

import lib.minio.MinioSrvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    private MinioSrvc minioService;

    public ResponseEntity<Object> getAllRecipes(int pageSize, int pageNumber, RecipeFilterRequestDTO recipeFiltersDTO) {
        Map<String, Object> result = new HashMap<>();
        HttpStatus status = HttpStatus.CREATED;
        String message = "";
        RecipeFilterMethod recipeFilterMethod = new RecipeFilterMethod();

        try {

            recipeFilterMethod.filterRecipe(recipeListRepo, favoriteFoodsRepo, result, status, pageSize, pageNumber,
                    recipeFiltersDTO,
                    minioService);

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Failed to Get Data!";

            result.put("error", e.getMessage());
            result.put("message", message);
            result.put("status", status);

        }
        return ResponseEntity.status(status).body(result);
    }

    public ResponseEntity<Object> toggleFavorite(int recipeId, int userId) {
        Map<String, Object> result = new HashMap<>();
        HttpStatus status = HttpStatus.CREATED;
        String message = "Resep " + recipeId + " berhasil ditambahkan ke dalam favorit";

        try {
            Optional<Recipes> recipesData = recipeRepo.findById(recipeId);
            Optional<Users> usersData = userRepo.findById(userId);
            Recipes recipe = recipesData.get();
            Users user = usersData.get();

            if (recipe == null) {
                status = HttpStatus.NOT_FOUND;
                message = "Data dengan id " + recipeId + " tidak ditemukan!";
            } else {
                String recipeName = recipe.getRecipeName();
                Optional<FavoriteFoods> favoriteFoods = favoriteFoodsRepo
                        .findMyFavorite(recipeId, userId);

                if (!favoriteFoods.isPresent()) {
                    FavoriteFoodsId favId = new FavoriteFoodsId();
                    FavoriteFoods favorite = new FavoriteFoods();
                    favId.setUserId(user.getUserId());
                    favId.setRecipeId(recipe.getRecipeId());
                    favorite.setIsFavorite(true);

                    favorite.setUsers(user);
                    favorite.setRecipes(recipe);
                    favorite.setId(favId);
                    favorite = favoriteFoodsRepo.save(favorite);
                    message = "Resep " + recipeName + " berhasil ditambahkan ke favorite!";
                } else if (favoriteFoods.isPresent()) {
                    if (favoriteFoods.get().getIsFavorite() == true) {
                        favoriteFoods.get().setIsFavorite(false);
                        favoriteFoodsRepo.save(favoriteFoods.get());
                        message = "Resep " + recipeName + " berhasil dihapus dari favorite!";
                    } else {
                        favoriteFoods.get().setIsFavorite(true);
                        favoriteFoodsRepo.save(favoriteFoods.get());
                        message = "Resep " + recipeName + " berhasil ditambahkan ke favorite!";
                    }
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
