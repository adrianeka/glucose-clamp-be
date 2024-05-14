package com.tujuhsembilan.bookrecipe.controller.bookrecipe;

import com.tujuhsembilan.bookrecipe.dto.request.*;
import com.tujuhsembilan.bookrecipe.dto.response.MessageResponse;
import com.tujuhsembilan.bookrecipe.dto.response.ResponseBodyDTO;
import com.tujuhsembilan.bookrecipe.service.RecipeListService;
import com.tujuhsembilan.bookrecipe.service.RecipesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Book Recipe", description = "Book Recipe Management APIs")
@RestController
@RequestMapping("/book-recipe")
public class RecipesController {

    @Autowired
    private RecipesService recipeService;

    @Autowired
    private RecipeListService recipeListService;

    @GetMapping("/my-recipes")
    public ResponseEntity<Object> getResepSaya(
            @PageableDefault(page = 1, size = 8, sort = "recipeName", direction = Direction.ASC) Pageable page,
            @ModelAttribute MyRecipeRequestDTO myRecipesDTO) {
        return recipeService.getResepSaya(myRecipesDTO, page);
    }

    @PutMapping("/book-recipes/{recipeId}")
    public ResponseEntity<Object> deleteResepSayaById(@PathVariable int recipeId, @RequestParam int userId) {
        return recipeService.deleteResepSaya(recipeId, userId);
    }

    @GetMapping("/book-recipes")
    public ResponseEntity<Object> getAllRecipes(
            @PageableDefault(page = 1, size = 8, sort = "recipeName", direction = Direction.ASC) Pageable page,
            @ModelAttribute RecipeFilterRequestDTO recipeFiltersDTO) {
        return recipeListService.getAllRecipes(page, recipeFiltersDTO);
    }

    @PutMapping("/book-recipes/{recipeId}/favorites")
    public ResponseEntity<Object> toggleFavorite(@PathVariable(name = "recipeId") int recipeId,
            @RequestBody FavoriteRecipeDTO favoriteRecipeDtO) {
        return recipeListService.toggleFavorite(recipeId, favoriteRecipeDtO.getUserId());
    }

    @GetMapping("/my-favorite-recipes")
    public ResponseEntity<Object> getUserFavoriteRecipe(
            @PageableDefault(page = 1, size = 8, sort = "recipeName", direction = Direction.ASC) Pageable page,
            @ModelAttribute RecipeFilterDTO filter) {
        Object response = recipeService.getDataByIdWithFilterAndSort(filter, page);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = { "/book-recipes" }, consumes = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<MessageResponse> createRecipe(
            @RequestPart("request") CreateRecipeRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        int userId = request.getUserId();
        MessageResponse response = recipeService.create(request, file, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping(path = { "/book-recipes" }, consumes = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<MessageResponse> updateRecipe(
            @RequestPart("request") UpdateRecipeRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        int userId = request.getUserId();
        MessageResponse response = recipeService.updateRecipeById(request, file, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/book-recipes/{recipeId}")
    public ResponseEntity<ResponseBodyDTO> getRecipeById(@PathVariable int recipeId) {
        ResponseBodyDTO response = recipeService.getRecipeById(recipeId);
        return new ResponseEntity<>(response, HttpStatus.valueOf((int) response.getStatusCode()));
    }
}
