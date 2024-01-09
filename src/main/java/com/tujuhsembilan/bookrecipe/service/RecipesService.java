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
import com.tujuhsembilan.bookrecipe.dto.request.UpdateRecipeRequest;
import com.tujuhsembilan.bookrecipe.dto.response.MessageResponse;
import com.tujuhsembilan.bookrecipe.model.Recipes;
import com.tujuhsembilan.bookrecipe.model.Users;
import com.tujuhsembilan.bookrecipe.model.Categories;
import com.tujuhsembilan.bookrecipe.model.Levels;
import com.tujuhsembilan.bookrecipe.repository.CategoriesRepository;
import com.tujuhsembilan.bookrecipe.repository.LevelsRepository;
import com.tujuhsembilan.bookrecipe.repository.RecipesRepository;
import com.tujuhsembilan.bookrecipe.repository.UsersRepository;

import java.sql.Timestamp;
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
    private UsersRepository usersRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String minioBucketName;

    @Transactional
    public MessageResponse create(CreateRecipeRequest request, MultipartFile imageFile) {
        validationService.validate(request);

        Users createdByUser = usersRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with id: " + 1));

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
        newRecipe.setImageFilename(imageFilename); 
        newRecipe.setTimeCook(request.getTimeCook());
        newRecipe.setIngridient(request.getIngridient());
        newRecipe.setHowToCook(request.getHowToCook());
        newRecipe.setCreatedBy(createdByUser.getUsername());
        newRecipe.setModifiedBy(createdByUser.getUsername());
        newRecipe.setIsDeleted(false);
        newRecipe.setCreatedTime(new Timestamp(System.currentTimeMillis()));
        newRecipe.setModifiedTime(new Timestamp(System.currentTimeMillis()));


        // Simpan ke repository atau database
        recipesRepository.save(newRecipe);

        String responseMessage = "Resep " + request.getRecipeName() + " berhasil ditambahkan!";
        int statusCode = HttpStatus.OK.value();
        String status = HttpStatus.OK.getReasonPhrase();

        log.info(responseMessage, statusCode, status);

        return new MessageResponse(responseMessage, statusCode, status);
    }

    @Transactional
    public MessageResponse updateRecipeById(UpdateRecipeRequest request, MultipartFile imageFile) {
        validationService.validate(request);

        // Retrieve the current user information from the security context
        Users modifiedByUser = usersRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + 1));

        // Find the existing recipe by ID
        Recipes existingRecipe = recipesRepository.findById(request.getRecipeId())
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with id: " + request.getRecipeId()));

        // Update the recipe fields
        Categories categories = categoriesRepository.findById(request.getCategories().getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Categories not found with id: " + request.getCategories().getCategoryId()));
        Levels levels = levelsRepository.findById(request.getLevels().getLevelId())
                .orElseThrow(() -> new EntityNotFoundException("Levels not found with id: " + request.getLevels().getLevelId()));

        existingRecipe.setCategories(categories);
        existingRecipe.setLevels(levels);
        existingRecipe.setRecipeName(request.getRecipeName());

        // Update image if provided
        if (imageFile != null) {
            try {
                String newImageFilename = updateImageToMinio(request, imageFile);
                existingRecipe.setImageFilename(newImageFilename);
            } catch (IOException e) {
                String errorMessage = "Failed to upload image to MinIO";
                log.error(errorMessage, e);
                return new MessageResponse(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            }
        }

        existingRecipe.setTimeCook(request.getTimeCook());
        existingRecipe.setIngridient(request.getIngridient());
        existingRecipe.setHowToCook(request.getHowToCook());
        existingRecipe.setModifiedBy(modifiedByUser.getUsername());
        existingRecipe.setModifiedTime(new Timestamp(System.currentTimeMillis()));

        // Save the updated recipe
        recipesRepository.save(existingRecipe);

        String responseMessage = "Resep " + request.getRecipeName() + " berhasil diubah!";
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

    private String updateImageToMinio(UpdateRecipeRequest request, MultipartFile imageFile) throws IOException {
        String recipeName = sanitizeForFilename(request.getRecipeName());
        String categoryName = sanitizeForFilename(request.getCategories().getCategoryName());
        String levelName = sanitizeForFilename(request.getLevels().getLevelName());

        if (recipeName.isEmpty() || categoryName.isEmpty() || levelName.isEmpty()) {
            log.warn("One or more components for filename are empty. Recipe: {}, Category: {}, Level: {}",
                    request.getRecipeName(), request.getCategories().getCategoryName(),
                    request.getLevels().getLevelName());
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
