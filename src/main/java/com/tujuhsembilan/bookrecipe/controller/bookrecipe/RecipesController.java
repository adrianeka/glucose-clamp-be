package com.tujuhsembilan.bookrecipe.controller.bookrecipe;

import com.tujuhsembilan.bookrecipe.dto.ErrorDTO;
import com.tujuhsembilan.bookrecipe.dto.request.MyRecipeRequestDTO;
import com.tujuhsembilan.bookrecipe.service.RecipesService;
import com.tujuhsembilan.bookrecipe.service.spesification.filter.RecipeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}
