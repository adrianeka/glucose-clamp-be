package com.tujuhsembilan.bookrecipe.controller.bookrecipe;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.tujuhsembilan.bookrecipe.dto.ErrorDTO;
import com.tujuhsembilan.bookrecipe.dto.request.CreateRecipeRequest;
import com.tujuhsembilan.bookrecipe.dto.request.MyRecipeRequestDTO;
import com.tujuhsembilan.bookrecipe.dto.request.RecipeFilterRequestDTO;
import com.tujuhsembilan.bookrecipe.dto.request.UpdateRecipeRequest;
import com.tujuhsembilan.bookrecipe.dto.response.MessageResponse;
import com.tujuhsembilan.bookrecipe.service.RecipeListService;
import com.tujuhsembilan.bookrecipe.service.RecipesService;
import com.tujuhsembilan.bookrecipe.service.specification.filter.RecipeFilter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Slf4j
@Tag(name = "Book Recipe", description = "Book Recipe Management APIs")
@RestController
@RequestMapping("/book-recipe/book-recipes")
public class RecipesController {

	@Autowired
	private RecipesService recipeService;

	@Autowired
	private RecipeListService recipeListService;

	@GetMapping("/my-recipes")
	public ResponseEntity<Object> getResepSaya(@ModelAttribute MyRecipeRequestDTO myRecipesDTO,
			@RequestParam(required = false) String sortBy,
			@RequestParam(required = false, defaultValue = "8") int pageSize,
			@RequestParam(required = false, defaultValue = "1") int pageNumber) {
		//try {
			return recipeService.getResepSaya(myRecipesDTO, sortBy, pageSize, pageNumber);
			/*
		} catch (NullPointerException e) {
			e.printStackTrace();

			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Resep Masakkan Tidak Tersedia");
		}
		*/
	}

	@PutMapping("/{recipeId}")
	public ResponseEntity<Object> deleteResepSayaById(@PathVariable int recipeId, @RequestParam int userId) {
		return recipeService.deleteResepSaya(recipeId, userId);
	}

	@GetMapping("")
	public ResponseEntity<Object> getAllRecipes(@RequestParam(required = false, defaultValue = "8") int pageSize,
			@RequestParam(required = false, defaultValue = "1") int pageNumber,
			@ModelAttribute RecipeFilterRequestDTO recipeFiltersDTO) {
		return recipeListService.getAllRecipes(pageSize, pageNumber, recipeFiltersDTO);
	}

	@PutMapping("/{recipeId}/favorites/{userId}")
	public ResponseEntity<Object> toggleFavorite(@PathVariable(name = "recipeId") int recipeId,
			@PathVariable(name = "userId") int userId) {
		return recipeListService.toggleFavorite(recipeId, userId);
	}

    @GetMapping("/my-favorite-recipes")
    public ResponseEntity<Object> getUserFavoriteRecipe(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "8") int pageSize,
            @ModelAttribute RecipeFilter filter
    ) {
        Object response = recipeService.getDataByIdWithFilterAndSort(page, pageSize, filter);
        if (response instanceof ErrorDTO) {
            return ResponseEntity.status(((ErrorDTO) response).getStatusCode())
                    .body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(
        consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE },
        produces = { MediaType.APPLICATION_JSON_VALUE }
    )    
    public ResponseEntity<MessageResponse> createRecipe(
            @RequestParam("userId") int userId,
            @RequestPart("request") CreateRecipeRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            MessageResponse response = recipeService.create(request, file, userId);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            log.error("Error", e);
            return ResponseEntity.status(500)
                    .body(new MessageResponse("Terjadi kesalahan server. Silakan coba kembali", 500, "ERROR"));
        }
    }

    @PutMapping( 
        consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE }, 
        produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<MessageResponse> updateRecipe(
            @RequestParam("userId") int userId,
            @RequestPart("request") UpdateRecipeRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            MessageResponse response = recipeService.updateRecipeById(request, file, userId);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            log.error("Error", e);
            return ResponseEntity.status(500)
                    .body(new MessageResponse("Terjadi kesalahan server. Silakan coba kembali", 500, "ERROR"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRecipeById(@PathVariable int id) {
        Map<String, Object> response = recipeService.getRecipeById(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf((int) response.get("statusCode")));
    }
}
