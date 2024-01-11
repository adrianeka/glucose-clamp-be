package com.tujuhsembilan.bookrecipe.controller.bookrecipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.tujuhsembilan.bookrecipe.dto.ErrorDTO;
import com.tujuhsembilan.bookrecipe.dto.request.MyRecipeRequestDTO;
import com.tujuhsembilan.bookrecipe.service.RecipesService;
import com.tujuhsembilan.bookrecipe.service.spesification.filter.RecipeFilter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.tujuhsembilan.bookrecipe.dto.request.CreateRecipeRequest;
import com.tujuhsembilan.bookrecipe.dto.request.UpdateRecipeRequest;
import com.tujuhsembilan.bookrecipe.dto.response.MessageResponse;
import com.tujuhsembilan.bookrecipe.service.RecipesService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/book-recipe/book-recipes")
public class RecipesController {

    @Autowired
    private RecipesService recipeService;

    @GetMapping("/my-recipes")
    public ResponseEntity<Object> getResepSaya(@ModelAttribute MyRecipeRequestDTO myRecipesDTO,
											   @RequestParam(required = false) String sortBy,
											   @RequestParam(required = false, defaultValue = "1") Integer pageSize,
											   @RequestParam(required = false, defaultValue = "8") Integer pageNumber) {

        try {
            return recipeService.getResepSaya(myRecipesDTO, sortBy, pageSize, pageNumber);

        } catch (NullPointerException e) {
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Resep Masakkan Tidak Tersedia");
        }

    }

    @PutMapping("/{recipeId}")
    public ResponseEntity<Object> deleteResepSayaById(@PathVariable int recipeId, @RequestParam int userId) {

        return recipeService.deleteResepSaya(recipeId, userId);
    }

    @GetMapping("/my-favorite-recipes")
    public ResponseEntity<Object> getUserFavoriteRecipe(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @ModelAttribute RecipeFilter filter
    ) {
        Object response = recipeService.getDataByIdWithFilterAndSort(page, pageSize, filter);
        if (response instanceof ErrorDTO) {
            return ResponseEntity.status(((ErrorDTO) response).getStatusCode())
                    .body(response);
        }
        return ResponseEntity.ok(response);
    }


    
    @Autowired
    private RecipesService recipesService;

    @PostMapping(
        consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE },
        produces = { MediaType.APPLICATION_JSON_VALUE }
    )    
    public ResponseEntity<MessageResponse> createRecipe(
            @RequestPart("request") CreateRecipeRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            // Users users = new Users(); 
            MessageResponse response = recipesService.create(request, file);
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
            @RequestPart("request") UpdateRecipeRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            MessageResponse response = recipesService.updateRecipeById(request, file);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            log.error("Error", e);
            return ResponseEntity.status(500)
                    .body(new MessageResponse("Terjadi kesalahan server. Silakan coba kembali", 500, "ERROR"));
        }
    }
}
