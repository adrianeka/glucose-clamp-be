package com.tujuhsembilan.bookrecipe.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tujuhsembilan.bookrecipe.dto.request.CreateRecipeRequest;
import com.tujuhsembilan.bookrecipe.dto.response.MessageResponse;
import com.tujuhsembilan.bookrecipe.model.Recipes;
import com.tujuhsembilan.bookrecipe.model.Categories;
import com.tujuhsembilan.bookrecipe.model.Levels;
import com.tujuhsembilan.bookrecipe.repository.CategoriesRepository;
import com.tujuhsembilan.bookrecipe.repository.LevelsRepository;
import com.tujuhsembilan.bookrecipe.repository.RecipesRepository;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RecipesService {

    @Autowired
    private RecipesRepository recipesRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private LevelsRepository levelsRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String minioBucketName;

    @Transactional
    public MessageResponse create(CreateRecipeRequest request, MultipartFile imageFile) {
        validationService.validate(request);

        Categories categories = categoriesRepository.findById(request.getCategories().getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Categories not found with id: " + request.getCategories().getCategoryId()));

        Levels levels = levelsRepository.findById(request.getLevels().getLevelId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Levels not found with id: " + request.getLevels().getLevelId()));

        String imageFilename;
        try {
            imageFilename = uploadImageToMinio(request, imageFile);
        } catch (IOException e) {
            String errorMessage = "Failed to upload image to MinIO";
            log.error(errorMessage, e);
            return new MessageResponse(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
        log.info(imageFilename);

        Recipes newRecipe = new Recipes();
        newRecipe.setCategories(categories);
        newRecipe.setLevels(levels);
        newRecipe.setRecipeName(request.getRecipeName());
        newRecipe.setImageFilename(imageFilename); // Set nama file di model Recipes
        newRecipe.setTimeCook(request.getTimeCook());
        newRecipe.setIngridient(request.getIngridient());
        newRecipe.setHowToCook(request.getHowToCook());

        // Simpan ke repository atau database
        recipesRepository.save(newRecipe);

        String responseMessage = "Resep " + request.getRecipeName() + " berhasil ditambahkan!";
        int statusCode = HttpStatus.OK.value();
        String status = HttpStatus.OK.getReasonPhrase();

        log.info(responseMessage, statusCode, status);

        return new MessageResponse(responseMessage, statusCode, status);
    }

    private String uploadImageToMinio(CreateRecipeRequest request, MultipartFile imageFile) throws IOException {
        String recipeName = sanitizeForFilename(request.getRecipeName());
        String categoryName = sanitizeForFilename(request.getCategories().getCategoryName());
        String levelName = sanitizeForFilename(request.getLevels().getLevelName());

        if (recipeName.isEmpty() || categoryName.isEmpty() || levelName.isEmpty()) {
            log.warn("One or more components for filename are empty. Recipe: {}, Category: {}, Level: {}",
                    request.getRecipeName(), request.getCategories().getCategoryName(),
                    request.getLevels().getLevelName());
            // Handle the situation as per your requirements.
            // You might want to throw an exception, log a warning, or provide a default
            // value.
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileExtension = getFileExtension(imageFile.getOriginalFilename());

        String generatedFilename = String.format(
                "%s_%s_%s_%s%s",
                recipeName,
                categoryName,
                levelName,
                timestamp,
                fileExtension);

        try (InputStream inputStream = imageFile.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioBucketName)
                            .object(generatedFilename)
                            .stream(inputStream, imageFile.getSize(), -1)
                            .contentType(imageFile.getContentType())
                            .build());
        } catch (Exception e) {
            throw new IOException("Failed to upload image to MinIO", e);
        }

        log.info(generatedFilename);
        return generatedFilename;
    }

    private String sanitizeForFilename(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", "_");
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }
}
