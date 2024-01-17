package com.tujuhsembilan.bookrecipe.service.method;

import com.tujuhsembilan.bookrecipe.dto.request.RecipeFilterRequestDTO;
import com.tujuhsembilan.bookrecipe.dto.response.RecipeCategoryDTO;
import com.tujuhsembilan.bookrecipe.dto.response.RecipeLevelDTO;
import com.tujuhsembilan.bookrecipe.dto.response.RecipeResponseDTO;
import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;
import com.tujuhsembilan.bookrecipe.model.Recipes;
import com.tujuhsembilan.bookrecipe.repository.FavoriteFoodsRepository;
import com.tujuhsembilan.bookrecipe.repository.RecipeListRepository;
import com.tujuhsembilan.bookrecipe.service.specification.RecipeListSpecification;
import lib.minio.MinioSrvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecipeFilterMethod {

    public ResponseEntity<Object> filterRecipe(RecipeListRepository recipesListRepo,
            FavoriteFoodsRepository favoriteFoodsRepo,
            Map<String, Object> result, HttpStatus status,
            int pageSize, int pageNumber, RecipeFilterRequestDTO recipeFiltersDTO,
            MinioSrvc minioService) {

        Sort sorting = null;
        boolean isSortByEmpty = recipeFiltersDTO.getSortBy() == null;

        if (!isSortByEmpty) {
            if (recipeFiltersDTO.getSortBy().equals("recipeName-ASC")) {
                sorting = Sort.by(Sort.Order.asc("recipeName"));
            } else if (recipeFiltersDTO.getSortBy().equals("recipeName-DESC")) {
                sorting = Sort.by(Sort.Order.desc("recipeName"));
            } else if (recipeFiltersDTO.getSortBy().equals("time-ASC")) {
                sorting = Sort.by(Sort.Order.asc("timeCook"));
            } else if (recipeFiltersDTO.getSortBy().equals("time-DESC")) {
                sorting = Sort.by(Sort.Order.desc("timeCook"));
            }
        } else {
            sorting = Sort.by(Sort.Order.asc("recipeName"));
        }

        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize, sorting);
        Specification<Recipes> spec = RecipeListSpecification.recipesFilterAll(recipeFiltersDTO);
        Page<Recipes> recipesFiltered = recipesListRepo.findAll(spec, pageRequest);

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

        result.put("total", totalData);
        result.put("data", response);
        result.put("message", "Berhasil memuat Resep Masakan Saya");

        return ResponseEntity.status(status).body(result);
    }

    private Boolean getIsFavorite(FavoriteFoodsRepository favoriteFoodsRepo, Integer recipeId, Integer userId) {
        Optional<FavoriteFoods> favoriteFoods = favoriteFoodsRepo.findMyFavorite(recipeId, userId);
        if (favoriteFoods.isPresent()) {
            if (favoriteFoods.get().getIsFavorite() == true) {
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
