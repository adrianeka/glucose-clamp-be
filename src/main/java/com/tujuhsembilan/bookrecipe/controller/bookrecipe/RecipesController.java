package com.tujuhsembilan.bookrecipe.controller.bookrecipe;

import com.tujuhsembilan.bookrecipe.dto.ErrorDTO;
import com.tujuhsembilan.bookrecipe.dto.bookrecipe.DisplayPaginationRecipeFav;
import com.tujuhsembilan.bookrecipe.service.RecipesService;
import com.tujuhsembilan.bookrecipe.spesification.filter.RecipeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/book-recipe")
public class RecipesController {

    @Autowired
    private RecipesService recipesService;

    @GetMapping("/my-favorite-recipes")
    public ResponseEntity<Object> getUserFavoriteRecipe(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @ModelAttribute RecipeFilter filter
    ) {
        Object response = recipesService.getDataByIdWithFilterAndSort(page, pageSize, filter);
        if (response instanceof ErrorDTO) {
            return ResponseEntity.status(((ErrorDTO) response).getStatusCode())
                    .body(response);
        }
        return ResponseEntity.ok(response);
    }


}
