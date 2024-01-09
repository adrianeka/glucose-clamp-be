package com.tujuhsembilan.bookrecipe.controller.bookrecipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tujuhsembilan.bookrecipe.dto.request.MyRecipeRequestDTO;
import com.tujuhsembilan.bookrecipe.service.RecipesService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/book-recipe/book-recipes")
public class RecipesController {
    
	@Autowired
	private RecipesService recipeService;
	
	@GetMapping("/my-recipes")
	public ResponseEntity<Object> getResepSaya(@RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "8") int limit , @ModelAttribute MyRecipeRequestDTO myRecipesDTO, @RequestParam(required = false) String sortBy) {
		
		try{
			return recipeService.getResepSaya(page, limit, myRecipesDTO, sortBy);
			
		} catch(NullPointerException e) {
			e.printStackTrace();
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Resep Masakkan Tidak Tersedia");
		}
		
	}
	
	
}
