package com.tujuhsembilan.bookrecipe.service;

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
import lib.i18n.utility.MessageUtil;
import lib.minio.MinioSrvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    
    @Autowired
    private MessageUtil messageUtil;

    public ResponseEntity<Object> getAllRecipes(Pageable page, RecipeFilterRequestDTO recipeFiltersDTO) {
        Map<String, Object> result = new HashMap<>();
        HttpStatus status = HttpStatus.CREATED;
        String message = "";
        RecipeFilterMethod recipeFilterMethod = new RecipeFilterMethod();

        try {

            recipeFilterMethod.filterRecipe(recipeListRepo, favoriteFoodsRepo, result, status, page,
                    recipeFiltersDTO,
                    minioService,
                    messageUtil);

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = messageUtil.get("application.error.recipe.not-found");

            result.put("error", e.getMessage());
            result.put("message", message);
            result.put("status", status);

        }
        return ResponseEntity.status(status).body(result);
    }

    public ResponseEntity<Object> toggleFavorite(int recipeId, int userId) {
        Map<String, Object> result = new HashMap<>();
        HttpStatus status = HttpStatus.CREATED;
        String message = messageUtil.get("application.success.add-favorite", recipeId);

        try {
            Optional<Recipes> recipesData = recipeRepo.findById(recipeId);
            Optional<Users> usersData = userRepo.findById(userId);

            if (recipesData.isEmpty() && usersData.isEmpty()) {
                status = HttpStatus.NOT_FOUND;
                message = messageUtil.get("application.error.recipe.not-found", recipeId);
            } else {
            	Recipes recipe = recipesData.get();
                Users user = usersData.get();
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
                    message = messageUtil.get("application.success.add-favorite", recipeName);
                } else {
                    if (favoriteFoods.get().getIsFavorite().booleanValue()) {
                        favoriteFoods.get().setIsFavorite(false);
                        favoriteFoodsRepo.save(favoriteFoods.get());
                        message = messageUtil.get("application.success.delete-favorite", recipeName);
                    } else {
                        favoriteFoods.get().setIsFavorite(true);
                        favoriteFoodsRepo.save(favoriteFoods.get());
                        message = messageUtil.get("application.success.add-favorite", recipeName);
                    }
                }
            }

            result.put("total", recipeRepo.count());
            result.put("data", null);
            result.put("message", message);
            result.put("status", status);
            return ResponseEntity.status(status).body(result);

        } catch (Exception e) {
            message = messageUtil.get("application.error.internal");
            status = HttpStatus.INTERNAL_SERVER_ERROR;

            result.put("message", message);
            result.put("status", status);
            return ResponseEntity.status(status).body(result);
        }
    }
}
