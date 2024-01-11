package com.tujuhsembilan.bookrecipe.service.method;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lib.minio.MinioSrvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tujuhsembilan.bookrecipe.dto.request.RecipeFilterRequestDTO;
import com.tujuhsembilan.bookrecipe.dto.response.RecipeCategoryDTO;
import com.tujuhsembilan.bookrecipe.dto.response.RecipeLevelDTO;
import com.tujuhsembilan.bookrecipe.dto.response.RecipeResponseDTO;
import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;
import com.tujuhsembilan.bookrecipe.model.Recipes;
import com.tujuhsembilan.bookrecipe.repository.RecipeListRepository;
import com.tujuhsembilan.bookrecipe.service.specification.RecipeListSpecification;


public class RecipeFilterMethod {
    @Autowired
    private MinioSrvc minioService;

    private final String bucket = "talent79-dev";

    public ResponseEntity<Object> filterRecipe(RecipeListRepository recipesListRepo,
            Map<String, Object> result, HttpStatus status,
            int pageSize, int pageNumber, RecipeFilterRequestDTO recipeFiltersDTO) {

        Sort sorting = null;
        Boolean isSortByEmpty = recipeFiltersDTO.getSortBy() == null;

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

        List<RecipeResponseDTO> response = recipesFiltered.stream().map(recipe -> 
			new RecipeResponseDTO(
					recipe.getRecipeId(),
					new RecipeCategoryDTO(recipe.getCategories().getCategoryId(), recipe.getCategories().getCategoryName()),
					new RecipeLevelDTO(recipe.getLevels().getLevelId(), recipe.getLevels().getLevelName()),
					recipe.getRecipeName(),
                    getImageURL(bucket, recipe.getImageFilename()),
					recipe.getTimeCook(),
                    getIsFavorite(recipe)			
				))
			.collect(Collectors.toList());
		
        result.put("total", totalData);
        result.put("data", response);
        result.put("message", "Berhasil memuat Resep Masakan Saya");

        return ResponseEntity.status(status).body(result);
    }

    private Boolean getIsFavorite(Recipes recipe) {
        Set<FavoriteFoods> favoriteFoodses = recipe.getFavoriteFoodses();
        if (favoriteFoodses != null && !favoriteFoodses.isEmpty()) {
            FavoriteFoods firstFavorite = favoriteFoodses.iterator().next();
            if (firstFavorite != null && firstFavorite.getId() != null) {
                return firstFavorite.getId().getIsFavorite();
            }
        }
        return false;
    }

    private String getImageURL(String bucket, String filename) {
        String url = "";

        if(bucket != null && filename != null) {
            url = minioService.getPublicLink(bucket, filename);
        }

        return url;
    }
    
}
