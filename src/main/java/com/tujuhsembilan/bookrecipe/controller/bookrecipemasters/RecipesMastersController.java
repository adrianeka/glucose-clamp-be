package com.tujuhsembilan.bookrecipe.controller.bookrecipemasters;

import com.tujuhsembilan.bookrecipe.dto.CategoriesDTO;
import com.tujuhsembilan.bookrecipe.dto.LevelsDTO;
import com.tujuhsembilan.bookrecipe.dto.response.ListResponse;
import com.tujuhsembilan.bookrecipe.model.Levels;
import com.tujuhsembilan.bookrecipe.service.CategoriesService;
import com.tujuhsembilan.bookrecipe.service.LevelsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lib.i18n.utility.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Book Recipe Master", description = "Book Recipe Master Management APIs")
@RestController
@RequestMapping("/book-recipe-masters")
public class RecipesMastersController {
    @Autowired
    private CategoriesService categoryService;

    @Autowired
    private LevelsService levelsService;

    @Autowired
    private MessageUtil messageUtil;

    @Operation(
            summary = "Retrieve Category Option List",
            description = "Get List of Master Data Category")
    @GetMapping("/category-option-lists")
    public ResponseEntity<ListResponse<CategoriesDTO>> getCategoryOptions() {
        List<CategoriesDTO> categoryDTOs = categoryService.getAllCategories().stream()
                .map(category -> new CategoriesDTO(category.getCategoryId(), category.getCategoryName()))
                .collect(Collectors.toList());

        ListResponse<CategoriesDTO> response = new ListResponse<>(categoryDTOs, messageUtil.get("application.success.get"), HttpStatus.OK.value(), "Success");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Retrieve Levels Option List",
            description = "Get List of Master Data Level")
    @GetMapping("/level-option-lists")
    public ResponseEntity<ListResponse<LevelsDTO>> getLevelOptions() {
        List<Levels> levels = levelsService.getAllLevels();

        List<LevelsDTO> levelDTOs = levels.stream()
                .map(level -> new LevelsDTO(level.getLevelId(), level.getLevelName()))
                .collect(Collectors.toList());

        ListResponse<LevelsDTO> response = new ListResponse<>(levelDTOs, 
                        messageUtil.get("application.success.get"), HttpStatus.OK.value(), "Success");
        return ResponseEntity.ok(response);
    }
}
