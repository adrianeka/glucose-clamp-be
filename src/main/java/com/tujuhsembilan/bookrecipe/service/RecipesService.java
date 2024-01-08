package com.tujuhsembilan.bookrecipe.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tujuhsembilan.bookrecipe.dto.request.MyRecipeRequestDTO;
import com.tujuhsembilan.bookrecipe.dto.response.MyRecipeCategoriesDTO;
import com.tujuhsembilan.bookrecipe.dto.response.MyRecipeResDTO;
import com.tujuhsembilan.bookrecipe.dto.response.MyRecipesLevelsDTO;
import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;
import com.tujuhsembilan.bookrecipe.model.Recipes;
import com.tujuhsembilan.bookrecipe.repository.FavoriteFoodsRepository;
import com.tujuhsembilan.bookrecipe.repository.RecipesRepository;
import com.tujuhsembilan.bookrecipe.service.spesification.RecipeSpesification;

@Service
public class RecipesService {
	
	@Autowired
    private RecipesRepository recipeRepo;
	
	@Autowired
	private FavoriteFoodsRepository favoriteRepo;
	
	public ResponseEntity<Object> getResepSaya(MyRecipeRequestDTO myRecipesDTO){
		Sort sortByNameAsc = Sort.by(Sort.Direction.ASC, "recipeName");
		Specification<Recipes> recipeSpec = RecipeSpesification.recipeFilter(myRecipesDTO);
		
		List<Recipes> recipes = recipeRepo.findAll(recipeSpec, sortByNameAsc);
		List<MyRecipeResDTO> response = recipes.stream().map(recipe -> 
			new MyRecipeResDTO(
					recipe.getRecipeId(),
					new MyRecipeCategoriesDTO(recipe.getCategories().getCategoryId(), recipe.getCategories().getCategoryName()),
					new MyRecipesLevelsDTO(recipe.getLevels().getLevelId(), recipe.getLevels().getLevelName()),
					recipe.getRecipeName(),
					recipe.getImageFilename(),
					recipe.getTimeCook(),
					getFavFood(recipe.getRecipeId(), recipe.getUsers().getUserId()).getId().getIsFavorite()
				))
			.collect(Collectors.toList());
		
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		result.put("total", recipes.size());
		result.put("data", response);
		result.put("message", "Berhasil memuat Resep Masakan Saya");
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	public FavoriteFoods getFavFood(Integer recipeId, Integer userId) {
		FavoriteFoods favFood = favoriteRepo.findById_RecipeIdAndId_UserId(recipeId, userId).orElse(null);
		
		return favFood;
	}
}
