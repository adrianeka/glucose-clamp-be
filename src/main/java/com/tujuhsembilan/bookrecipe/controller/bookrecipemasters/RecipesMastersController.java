package com.tujuhsembilan.bookrecipe.controller.bookrecipemasters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tujuhsembilan.bookrecipe.dto.CategoriesDTO;
import com.tujuhsembilan.bookrecipe.dto.LevelsDTO;
import com.tujuhsembilan.bookrecipe.dto.response.ListResponse;
import com.tujuhsembilan.bookrecipe.model.Levels;
import com.tujuhsembilan.bookrecipe.service.CategoriesService;
import com.tujuhsembilan.bookrecipe.service.LevelsService;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/book-recipe-masters")
public class RecipesMastersController {
    @Autowired
    private CategoriesService categoryService;

    @Autowired
    private LevelsService levelsService;

    @GetMapping("/category-option-lists")
    public ResponseEntity<ListResponse<CategoriesDTO>> getCategoryOptions() {
        List<CategoriesDTO> categoryDTOs = categoryService.getAllCategories().stream()
                .map(category -> new CategoriesDTO(category.getCategoryId(), category.getCategoryName()))
                .collect(Collectors.toList());

        ListResponse<CategoriesDTO> response = new ListResponse<>(categoryDTOs, "Pesan Sukses", HttpStatus.OK.value(), "Success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/level-option-lists")
    public ResponseEntity<ListResponse<LevelsDTO>> getLevelOptions() {
        List<Levels> levels = levelsService.getAllLevels();

        List<LevelsDTO> levelDTOs = levels.stream()
                .map(level -> new LevelsDTO(level.getLevelId(), level.getLevelName()))
                .collect(Collectors.toList());

        ListResponse<LevelsDTO> response = new ListResponse<>(levelDTOs, "Pesan Sukses", HttpStatus.OK.value(), "Success");
        return ResponseEntity.ok(response);
    }
}
