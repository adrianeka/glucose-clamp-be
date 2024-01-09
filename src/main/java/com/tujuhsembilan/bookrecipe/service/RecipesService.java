package com.tujuhsembilan.bookrecipe.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
	
	public ResponseEntity<Object> getResepSaya(int page, int limit, MyRecipeRequestDTO myRecipesDTO, String sortBy){
		Sort sortByNameAsc = Sort.by(Sort.Direction.ASC, "recipeName");
		Sort sortByNameDesc = Sort.by(Sort.Direction.DESC, "recipeName");
		Sort sortByTimeAsc = Sort.by(Sort.Direction.ASC, "timeCook");
		Sort sortByTimeDesc = Sort.by(Sort.Direction.DESC, "timeCook");
		
		int newPage = page - 1;
		
		Sort choosenSort = null;
		
		boolean isSortByEmpty = (sortBy == null);
		
		if(!isSortByEmpty) {
			switch(sortBy) {
				case "nameAsc":
					choosenSort = sortByNameAsc;
					break;
				case "nameDesc":
					choosenSort = sortByNameDesc;
					break;
				case "timeAsc":
					choosenSort = sortByTimeAsc;
					break;
				case "timeDesc":
					choosenSort = sortByTimeDesc;
					break;
			}
		} else {
			choosenSort = sortByNameAsc;
		}
		
		PageRequest pageRequest = PageRequest.of(newPage, limit, choosenSort);
		
		Specification<Recipes> recipeSpec = RecipeSpesification.recipeFilter(myRecipesDTO);
		
		Page<Recipes> recipes = recipeRepo.findAll(recipeSpec, pageRequest);
		List<MyRecipeResDTO> response = recipes.stream().map(recipe -> 
			new MyRecipeResDTO(
					recipe.getRecipeId(),
					new MyRecipeCategoriesDTO(recipe.getCategories().getCategoryId(), recipe.getCategories().getCategoryName()),
					new MyRecipesLevelsDTO(recipe.getLevels().getLevelId(), recipe.getLevels().getLevelName()),
					recipe.getRecipeName(),
					recipe.getImageFilename(),
					recipe.getTimeCook(),
					getFavFood(recipe.getRecipeId(), recipe.getUsers().getUserId())
				))
			.collect(Collectors.toList());
		
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		result.put("total", response.size());
		result.put("data", response);
		result.put("message", "Berhasil memuat Resep Masakan Saya");
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	private Boolean getFavFood(Integer recipeId, Integer userId) {
		FavoriteFoods favFood = favoriteRepo.findById_RecipeIdAndId_UserId(recipeId, userId).orElse(null);
		Boolean isFavorite = false;
		
		if (favFood != null) {
			return favFood.getId().getIsFavorite();
		}
		
		return isFavorite;
	}
	
	public ResponseEntity<Object> deleteResepSaya(int recipeId, int userId){
		Recipes resepSaya = recipeRepo.findByRecipeIdAndUsers_UserId(recipeId, userId).orElse(null);
		
		String message = "";
		Integer jumlahResepDihapus = 0;
		
		if(resepSaya != null) {
			jumlahResepDihapus = 1;
			resepSaya.setIsDeleted(true);
			recipeRepo.save(resepSaya);
			message = "Resep " + resepSaya.getRecipeName() + " berhasil dihapus";
		} else {
			message = "Resep tidak ditemukan!";
		}
		
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		result.put("total", jumlahResepDihapus);
		result.put("data", "");
		result.put("message", message);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
