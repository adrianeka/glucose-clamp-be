package com.tujuhsembilan.bookrecipe.service;

import com.tujuhsembilan.bookrecipe.dto.ErrorDTO;
import com.tujuhsembilan.bookrecipe.dto.bookrecipe.CategoryFav;
import com.tujuhsembilan.bookrecipe.dto.bookrecipe.DisplayPaginationRecipeFav;
import com.tujuhsembilan.bookrecipe.dto.bookrecipe.LevelFav;
import com.tujuhsembilan.bookrecipe.dto.bookrecipe.UserFav;
import com.tujuhsembilan.bookrecipe.model.Categories;
import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;
import com.tujuhsembilan.bookrecipe.model.Levels;
import com.tujuhsembilan.bookrecipe.repository.FavoriteFoodsRepository;
import com.tujuhsembilan.bookrecipe.security.service.UserDetailsImplement;
import com.tujuhsembilan.bookrecipe.spesification.filter.RecipeFilter;
import com.tujuhsembilan.bookrecipe.spesification.spesification.RecipeSpecification;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RecipesService {


    @Autowired
    private FavoriteFoodsRepository favoriteFoodsRepository;

    public Object getDataByIdWithFilterAndSort(int page, int pageSize, RecipeFilter filter) {
        DisplayPaginationRecipeFav response = new DisplayPaginationRecipeFav();
        try {
            UserDetailsImplement userDetails = (UserDetailsImplement) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();
            log.info("Read Recipes with User id " + userDetails.getId() + " Success!");

            RecipeSpecification specification = new RecipeSpecification(filter);

            Page<FavoriteFoods> favoriteFoodsPage = favoriteFoodsRepository.findAll(
                    specification,
                    PageRequest.of(page, pageSize, specification.getSort())
            );

            List<UserFav> userFavList = favoriteFoodsPage.getContent().stream()
                    .filter(fav -> fav.getUsers().getUserId() == 1)
                    .map(this::mapFavoriteFoodsToUserFav)
                    .collect(Collectors.toList());

            if (userFavList.isEmpty()) {
                return new ErrorDTO(HttpStatus.NOT_FOUND.value(), "Data Not Found",
                        "User Not Found");
            }
            response.setTotal((int) favoriteFoodsPage.getTotalElements());
            response.setData(userFavList);
            response.setMessage("Success");
            response.setStatusCode(HttpStatus.OK.value());

        } catch (DataAccessException e) {
            return new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Data Access Error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected Error", e.getMessage());
        }
        return response;
    }


    private UserFav mapFavoriteFoodsToUserFav(FavoriteFoods favoriteFoods) {
        return Optional.ofNullable(favoriteFoods)
                .map(FavoriteFoods::getRecipes)
                .map(recipe -> {
                    UserFav userFav = new UserFav();
                    userFav.setRecipeId(recipe.getRecipeId());
                    userFav.setRecipeName(recipe.getRecipeName());
                    userFav.setImageUrl(recipe.getImageFilename());
                    userFav.setTime(recipe.getTimeCook());
                    userFav.setIs_favorite(favoriteFoods.getId().getIsFavorite());

                    Categories categories = recipe.getCategories();
                    Levels levels = recipe.getLevels();

                    if (categories != null && levels != null) {
                        LevelFav levelFav = new LevelFav();
                        levelFav.setLevelId(levels.getLevelId());
                        levelFav.setLevelName(levels.getLevelName());

                        CategoryFav categoryFav = new CategoryFav();
                        categoryFav.setCategoryId(categories.getCategoryId());
                        categoryFav.setCategoryName(categories.getCategoryName());

                        userFav.setCategories(categoryFav);
                        userFav.setLevels(levelFav);
                    } else {
                        log.error("Categories or Levels are null for recipeId: " + recipe.getRecipeId());
                    }
                    return userFav;
                })
                .orElseGet(() -> {
                    log.error("Data is null");
                    return new UserFav();
                });
    }

}
