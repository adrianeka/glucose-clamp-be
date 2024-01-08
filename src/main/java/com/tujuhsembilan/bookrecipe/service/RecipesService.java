package com.tujuhsembilan.bookrecipe.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tujuhsembilan.bookrecipe.dto.RecipesDTO;
import com.tujuhsembilan.bookrecipe.repository.RecipesRepository;
import com.tujuhsembilan.bookrecipe.service.spesification.RecipeSpesification;

@Service
public class RecipesService {
	
	@Autowired
    private RecipesRepository recipeRepo;
	
	public ResponseEntity<Object> getResepSaya(int userId, String namaResep){
		Sort sortByNameAsc = Sort.by(Sort.Direction.ASC, "recipeName");
		Specification<RecipesDTO> recipeSpec = RecipeSpesification.recipeFilter(namaResep);
		
		List<RecipesDTO> recipes = recipeRepo.findByUsers_UserId(userId, sortByNameAsc, recipeSpec);
		
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		result.put("total", recipes.size());
		result.put("data", recipes);
		result.put("message", "Berhasil memuat Resep Masakan Saya");
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
