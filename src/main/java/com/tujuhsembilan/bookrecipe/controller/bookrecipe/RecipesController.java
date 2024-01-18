package com.tujuhsembilan.bookrecipe.controller.bookrecipe;

import com.tujuhsembilan.bookrecipe.dto.ErrorDTO;
import com.tujuhsembilan.bookrecipe.dto.request.*;
import com.tujuhsembilan.bookrecipe.dto.response.MessageResponse;
import com.tujuhsembilan.bookrecipe.dto.response.ResponseBodyDTO;
import com.tujuhsembilan.bookrecipe.service.RecipeListService;
import com.tujuhsembilan.bookrecipe.service.RecipesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lib.i18n.utility.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "Book Recipe", description = "Book Recipe Management APIs")
@RestController
@RequestMapping("/book-recipe/book-recipes")
public class RecipesController {

	@Autowired
	private RecipesService recipeService;

	@Autowired
	private RecipeListService recipeListService;

    @Autowired
    private MessageUtil messageUtil;

	@GetMapping("/my-recipes")
	public ResponseEntity<Object> getResepSaya(@ModelAttribute MyRecipeRequestDTO myRecipesDTO,
			@PageableDefault(page = 1, size = 8, sort = "recipeName", direction = Direction.ASC) Pageable page) {
		return recipeService.getResepSaya(myRecipesDTO, page);
	}

	@PutMapping("/{recipeId}")
	public ResponseEntity<Object> deleteResepSayaById(@PathVariable int recipeId, @RequestParam int userId) {
		return recipeService.deleteResepSaya(recipeId, userId);
	}

	@GetMapping("")
	public ResponseEntity<Object> getAllRecipes(
			@PageableDefault(page = 1, size = 8, sort = "recipeName", direction = Direction.ASC) Pageable page,
			@ModelAttribute RecipeFilterRequestDTO recipeFiltersDTO) {
		return recipeListService.getAllRecipes(page, recipeFiltersDTO);
	}

	@PutMapping("/{recipeId}/favorites/{userId}")
	public ResponseEntity<Object> toggleFavorite(@PathVariable(name = "recipeId") int recipeId,
			@PathVariable(name = "userId") int userId) {
		return recipeListService.toggleFavorite(recipeId, userId);
	}

    @GetMapping("/my-favorite-recipes")
    public ResponseEntity<Object> getUserFavoriteRecipe(
            @PageableDefault(page = 1, size = 8, sort = "recipes.recipeName", direction = Direction.ASC) Pageable page,
            @ModelAttribute RecipeFilterDTO filter
    ) {
        Object response = recipeService.getDataByIdWithFilterAndSort(filter, page);
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
                    .body(new MessageResponse(messageUtil.get("application.error.internal"), 500, "ERROR"));
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
                    .body(new MessageResponse(messageUtil.get("application.error.internal"), 500, "ERROR"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBodyDTO> getRecipeById(@PathVariable int id) {
        ResponseBodyDTO response = recipeService.getRecipeById(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf((int) response.getStatusCode()));
    }
}
