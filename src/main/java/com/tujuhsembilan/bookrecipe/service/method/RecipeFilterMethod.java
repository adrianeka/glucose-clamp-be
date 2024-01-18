package com.tujuhsembilan.bookrecipe.service.method;

import com.tujuhsembilan.bookrecipe.dto.request.RecipeFilterRequestDTO;
import com.tujuhsembilan.bookrecipe.dto.response.RecipeCategoryDTO;
import com.tujuhsembilan.bookrecipe.dto.response.RecipeLevelDTO;
import com.tujuhsembilan.bookrecipe.dto.response.RecipeResponseDTO;
import com.tujuhsembilan.bookrecipe.dto.response.ResponseBodyDTO;
import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;
import com.tujuhsembilan.bookrecipe.model.Recipes;
import com.tujuhsembilan.bookrecipe.repository.FavoriteFoodsRepository;
import com.tujuhsembilan.bookrecipe.repository.RecipeListRepository;
import com.tujuhsembilan.bookrecipe.service.specification.RecipeListSpecification;
import lib.i18n.utility.MessageUtil;
import lib.minio.MinioSrvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecipeFilterMethod {

    public ResponseEntity<Object> filterRecipe(RecipeListRepository recipesListRepo,
            FavoriteFoodsRepository favoriteFoodsRepo,
            ResponseBodyDTO result, HttpStatus status,
            Pageable page, RecipeFilterRequestDTO recipeFiltersDTO,
            MinioSrvc minioService,
            MessageUtil messageUtil
    		) {

        Specification<Recipes> spec = RecipeListSpecification.recipesFilterAll(recipeFiltersDTO);
        Page<Recipes> recipesFiltered = recipesListRepo.findAll(spec, page);

        long totalData = recipesListRepo.count(spec);

        List<RecipeResponseDTO> response = recipesFiltered.stream().map(recipe -> new RecipeResponseDTO(
                recipe.getRecipeId(),
                new RecipeCategoryDTO(recipe.getCategories().getCategoryId(), recipe.getCategories().getCategoryName()),
                new RecipeLevelDTO(recipe.getLevels().getLevelId(), recipe.getLevels().getLevelName()),
                recipe.getRecipeName(),
                getImageURL(minioService, recipe.getImageFilename()),
                recipe.getTimeCook(),
                getIsFavorite(favoriteFoodsRepo, recipe.getRecipeId(), recipeFiltersDTO.getUserId())))
                .collect(Collectors.toList());
        
        result.setTotal(totalData);
        result.setData(response);
        result.setMessage(messageUtil.get("application.success.load", "Resep Masakan Saya"));
        result.setStatusCode(status.value());
        result.setStatus(status.name());

        return ResponseEntity.status(status).body(result);
    }

    private Boolean getIsFavorite(FavoriteFoodsRepository favoriteFoodsRepo, Integer recipeId, Integer userId) {
        Optional<FavoriteFoods> favoriteFoods = favoriteFoodsRepo.findMyFavorite(recipeId, userId);
        if (favoriteFoods.isPresent()) {
            if (favoriteFoods.get().getIsFavorite().booleanValue()) {
                return true;
            }
        }
        return false;

    }

    private String getImageURL(MinioSrvc minioService, String filename) {
        String url = "";

        if (filename != null) {
            url = minioService.getPublicLink(filename);
        }

        return url;
    }

}
