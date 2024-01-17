package com.tujuhsembilan.bookrecipe.service;

import java.io.IOException;

import com.tujuhsembilan.bookrecipe.dto.CategoriesDTO;
import com.tujuhsembilan.bookrecipe.dto.LevelsDTO;
import com.tujuhsembilan.bookrecipe.dto.RecipesDTO;
import com.tujuhsembilan.bookrecipe.security.service.UserDetailsImplement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import com.tujuhsembilan.bookrecipe.dto.ErrorDTO;
import com.tujuhsembilan.bookrecipe.dto.bookrecipe.CategoryFav;
import com.tujuhsembilan.bookrecipe.dto.bookrecipe.DisplayPaginationRecipeFav;
import com.tujuhsembilan.bookrecipe.dto.bookrecipe.LevelFav;
import com.tujuhsembilan.bookrecipe.dto.bookrecipe.UserFav;
import com.tujuhsembilan.bookrecipe.dto.request.MyRecipeRequestDTO;
import com.tujuhsembilan.bookrecipe.dto.response.MyRecipeCategoriesDTO;
import com.tujuhsembilan.bookrecipe.dto.response.MyRecipeResDTO;
import com.tujuhsembilan.bookrecipe.dto.response.MyRecipesLevelsDTO;
import com.tujuhsembilan.bookrecipe.exception.classes.AlreadyDeletedException;
import com.tujuhsembilan.bookrecipe.exception.classes.DataNotFoundException;
import com.tujuhsembilan.bookrecipe.model.Categories;
import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;
import com.tujuhsembilan.bookrecipe.model.Levels;
import com.tujuhsembilan.bookrecipe.model.Recipes;
import com.tujuhsembilan.bookrecipe.repository.FavoriteFoodsRepository;
import com.tujuhsembilan.bookrecipe.repository.RecipesRepository;
import com.tujuhsembilan.bookrecipe.service.specification.FavoriteFoodSpecification;
import com.tujuhsembilan.bookrecipe.service.specification.RecipeSpesification;
import com.tujuhsembilan.bookrecipe.service.specification.filter.RecipeFilter;
import lib.minio.MinioSrvc;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tujuhsembilan.bookrecipe.dto.request.CreateRecipeRequest;
import com.tujuhsembilan.bookrecipe.dto.request.UpdateRecipeRequest;
import com.tujuhsembilan.bookrecipe.dto.response.MessageResponse;
import com.tujuhsembilan.bookrecipe.model.Users;
import com.tujuhsembilan.bookrecipe.repository.CategoriesRepository;
import com.tujuhsembilan.bookrecipe.repository.LevelsRepository;
import com.tujuhsembilan.bookrecipe.repository.UsersRepository;

import java.sql.Timestamp;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
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

    @Value("${application.minio.bucketName}")
    private String minioBucketName;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FavoriteFoodsRepository favoriteRepo;

    @Lazy
    @Autowired
    private MinioSrvc minioService;

    @Transactional
    public MessageResponse create(CreateRecipeRequest request, MultipartFile imageFile, int userId) {
        validationService.validate(request);

        Users createdByUser = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with id: " + userId));

        Categories categories = categoriesRepository.findById(request.getCategories().getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Categories not found with id: " + request.getCategories().getCategoryId()));

        Levels levels = levelsRepository.findById(request.getLevels().getLevelId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Levels not found with id: " + request.getLevels().getLevelId()));

        String imageFilename;
        try {
            imageFilename = minioService.uploadImageToMinio(request, imageFile, minioBucketName);
        } catch (IOException e) {
            String errorMessage = "Failed to upload image to MinIO";
            log.error(errorMessage, e);
            return new MessageResponse(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
        log.info(imageFilename);

        Recipes newRecipe = Recipes.builder()
                .users(createdByUser)
                .categories(categories)
                .levels(levels)
                .recipeName(request.getRecipeName())
                .imageFilename(imageFilename)
                .timeCook(request.getTimeCook())
                .ingridient(request.getIngridient())
                .howToCook(request.getHowToCook())
                .createdBy(createdByUser.getUsername())
                .modifiedBy(createdByUser.getUsername())
                .isDeleted(false)
                .createdTime(new Timestamp(System.currentTimeMillis()))
                .modifiedTime(new Timestamp(System.currentTimeMillis()))
                .build();

        // Simpan ke repository atau database
        recipesRepository.save(newRecipe);

        String responseMessage = "Resep " + request.getRecipeName() + " berhasil ditambahkan!";
        int statusCode = HttpStatus.OK.value();
        String status = HttpStatus.OK.getReasonPhrase();

        log.info(responseMessage, statusCode, status);

        return new MessageResponse(responseMessage, statusCode, status);
    }

    @Transactional
    public MessageResponse updateRecipeById(UpdateRecipeRequest request, MultipartFile imageFile, int userId) {
        validationService.validate(request);

        // Retrieve the current user information from the security context
        Users modifiedByUser = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

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
        existingRecipe.setTimeCook(request.getTimeCook());
        existingRecipe.setIngridient(request.getIngridient());
        existingRecipe.setHowToCook(request.getHowToCook());
        existingRecipe.setModifiedBy(modifiedByUser.getUsername());
        existingRecipe.setModifiedTime(new Timestamp(System.currentTimeMillis()));

        // Update image if provided
        if (imageFile != null) {
            try {
                String newImageFilename = minioService.updateImageToMinio(request, imageFile, minioBucketName);
                existingRecipe.setImageFilename(newImageFilename);
            } catch (IOException e) {
                String errorMessage = "Failed to upload image to MinIO";
                log.error(errorMessage, e);
                return new MessageResponse(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            }
        }
        
        // Save the updated recipe
        recipesRepository.save(existingRecipe);

        String responseMessage = "Resep " + request.getRecipeName() + " berhasil diubah!";
        int statusCode = HttpStatus.OK.value();
        String status = HttpStatus.OK.getReasonPhrase();

        log.info(responseMessage, statusCode, status);

        return new MessageResponse(responseMessage, statusCode, status);
    }

    public ResponseEntity<Object> getResepSaya(MyRecipeRequestDTO myRecipesDTO, String sortBy, int pageSize, int pageNumber) {
        Sort sortByNameAsc = Sort.by(Sort.Direction.ASC, "recipeName");
        Sort sortByNameDesc = Sort.by(Sort.Direction.DESC, "recipeName");
        Sort sortByTimeAsc = Sort.by(Sort.Direction.ASC, "timeCook");
        Sort sortByTimeDesc = Sort.by(Sort.Direction.DESC, "timeCook");

        int newPage = pageNumber - 1;

        Sort choosenSort = null;

        boolean isSortByEmpty = (sortBy == null);

        if (!isSortByEmpty) {
            switch (sortBy) {
                case "nameAsc":
                    choosenSort = sortByNameAsc;
                    break;
                case "nameDesc":
                    choosenSort = sortByNameDesc;
                    break;
                case "timeAsc":
                    choosenSort = sortByTimeAsc;
                    break;
                case "timeDesc":
                    choosenSort = sortByTimeDesc;
                    break;
            }
        } else {
            choosenSort = sortByNameAsc;
        }

        PageRequest pageRequest = PageRequest.of(newPage, pageSize, choosenSort);

        Specification<Recipes> recipeSpec = RecipeSpesification.recipeFilter(myRecipesDTO);

        Page<Recipes> recipes = recipesRepository.findAll(recipeSpec, pageRequest);
            
        if(recipes.isEmpty()) {
        	throw new DataNotFoundException("Resep Masakkan Tidak Tersedia");
        } else {
            long totalData = recipesRepository.count(recipeSpec);
            List<MyRecipeResDTO> response = recipes.stream().map(recipe -> new MyRecipeResDTO(
                            recipe.getRecipeId(),
                            new MyRecipeCategoriesDTO(recipe.getCategories().getCategoryId(),
                                    recipe.getCategories().getCategoryName()),
                            new MyRecipesLevelsDTO(recipe.getLevels().getLevelId(), recipe.getLevels().getLevelName()),
                            recipe.getRecipeName(),
                            getImageURL(minioBucketName, recipe.getImageFilename()),
                            recipe.getTimeCook(),
                            getFavFood(recipe.getRecipeId(), recipe.getUsers().getUserId())))
                    .collect(Collectors.toList());

            Map<String, Object> result = new LinkedHashMap<String, Object>();

            result.put("total", totalData);
            result.put("data", response);
            result.put("message", "Berhasil memuat Resep Masakan Saya");

            return new ResponseEntity<>(result, HttpStatus.OK);
       }

    }

    private boolean getFavFood(int recipeId, int userId) {
        Optional<Boolean> favFood = favoriteRepo.findIsFavorite(recipeId, userId);

        if (favFood.isPresent()) {
            return favFood.get();
        } else {
            return false;
        }
    }

    private String getImageURL(String bucket, String filename) {
        String url = "";

        if (bucket != null && filename != null) {
            url = minioService.getPublicLink(bucket, filename);
        }

        return url;
    }

    public ResponseEntity<Object> deleteResepSaya(int recipeId, int userId) {
    	Recipes resepSaya = recipesRepository.findByMyRecipe(recipeId, userId).orElseThrow(() -> new DataNotFoundException("Resep tidak ditemukan!"));
    	
        String message = "";
        Integer jumlahResepDihapus = 0;

        if(resepSaya.getIsDeleted()) {
        	throw new AlreadyDeletedException(resepSaya.getRecipeName() + " sudah terhapus!");
        } else {
        	jumlahResepDihapus = 1;
            resepSaya.setIsDeleted(true);
            recipesRepository.save(resepSaya);
            message = "Resep " + resepSaya.getRecipeName() + " berhasil dihapus";
        }

        Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("total", jumlahResepDihapus);
        result.put("data", "");
        result.put("message", message);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public Object getDataByIdWithFilterAndSort(int page, int pageSize, RecipeFilter filter) {
        DisplayPaginationRecipeFav response = new DisplayPaginationRecipeFav();

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetailsImplement) {

                FavoriteFoodSpecification specification = new FavoriteFoodSpecification(filter);

                PageRequest pageRequest = PageRequest.of(page - 1, pageSize, specification.getSort());
                Page<FavoriteFoods> favoriteFoodsPage = favoriteRepo.findAll(specification, pageRequest);

                List<UserFav> userFavList = favoriteFoodsPage.getContent().stream()
                        .map(this::mapFavoriteFoodsToUserFav)
                        .collect(Collectors.toList());
                
                if (userFavList.isEmpty() || userFavList == null) {
                    throw new DataNotFoundException("Data Empty");
                }

                response.setTotal(favoriteRepo.countByIsFavoriteAndUsersUserId(true, filter.getUserId()));
                response.setData(userFavList);
                response.setMessage("Success");
                response.setStatus("Success Get Data");
                response.setStatusCode(HttpStatus.OK.value());

            } else if (principal instanceof String) {
                return new ErrorDTO(HttpStatus.UNAUTHORIZED.value(), "Unauthorized",
                        "User not authenticated");
            }

        } catch (DataAccessException e) {
            return new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Data Access Error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected Error",
                    "cause :\n" + e.getCause() + "\n " + e.getMessage());
        }
        return response;
    }



    private UserFav mapFavoriteFoodsToUserFav(FavoriteFoods favoriteFoods) {
        return Optional.ofNullable(favoriteFoods)
                .map(FavoriteFoods::getRecipes)
                .map(recipe -> {
                    UserFav userFav = new UserFav();
                    userFav.setRecipeId(recipe.getRecipeId());
                    userFav.setRecipeName(recipe.getRecipeName());
                    String imageUrl = recipe.getImageFilename();
                    try {
                        imageUrl = minioService.getLink(minioBucketName, recipe.getImageFilename(), MinioSrvc.DEFAULT_EXPIRY);
                    } catch (Exception e) {
                        log.error("Error retrieving image URL for recipeId: " + recipe.getRecipeId(), e);
                    }
                    userFav.setImageUrl(imageUrl);
                    userFav.setTime(recipe.getTimeCook());
                    userFav.setIs_favorite(favoriteFoods.getIsFavorite());

                    Categories categories = recipe.getCategories();
                    Levels levels = recipe.getLevels();

                    if (categories != null && levels != null) {
                        LevelFav levelFav = new LevelFav();
                        levelFav.setLevelId(levels.getLevelId());
                        levelFav.setLevelName(levels.getLevelName());

                        CategoryFav categoryFav = new CategoryFav();
                        categoryFav.setCategoryId(categories.getCategoryId());
                        categoryFav.setCategoryName(categories.getCategoryName());

                        userFav.setCategories(categoryFav);
                        userFav.setLevels(levelFav);
                    } else {
                        log.error("Categories or Levels are null for recipeId: " + recipe.getRecipeId());
                    }
                    return userFav;
                })
                .orElseGet(() -> {
                    log.error("Data is null");
                    return new UserFav();
                });
    }

    public Map<String, Object> getRecipeById(int recipeId) {
        Map<String, Object> response = new HashMap<>();

        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();
            int userId = 1;

            if (principal instanceof UserDetailsImplement) {
                UserDetailsImplement userDetails = (UserDetailsImplement) principal;
                userId = userDetails.getId();

            }

            Optional<Recipes> recipeOptional = recipesRepository.findById(recipeId);

            if (recipeOptional.isPresent()) {
                Recipes recipe = recipeOptional.get();

                RecipesDTO recipesDTO = modelMapper.map(recipe, RecipesDTO.class);
                Map<String, Object> data = new HashMap<>();

                // Membuat map untuk kategori
                Map<String, Object> categoryData = new HashMap<>();
                CategoriesDTO categoriesDTO = recipesDTO.getCategories();

                if (categoriesDTO != null) {
                    categoryData.put("categoryId", categoriesDTO.getCategoryId());
                    categoryData.put("categoryName", categoriesDTO.getCategoryName());
                } else {
                    categoryData.put("categoryId", null);
                    categoryData.put("categoryName", null);
                }

                Map<String, Object> levelData = new HashMap<>();
                LevelsDTO levelDTO = recipesDTO.getLevels();

                if (levelDTO != null) {
                    levelData.put("levelId", levelDTO.getLevelId());
                    levelData.put("levelName", levelDTO.getLevelName());
                } else {
                    levelData.put("levelId", null);
                    levelData.put("levelName", null);
                }

                // Menambahkan data kategori ke dalam map utama
                data.put("recipeId", recipesDTO.getRecipeId());
                data.put("category", categoryData);
                data.put("levels", levelData);
                data.put("recipeName", recipesDTO.getRecipeName());
                data.put("imageUrl", getImageURL(minioBucketName, recipesDTO.getImageFilename()));
                data.put("time", recipesDTO.getTimeCook());
                data.put("ingredient", recipesDTO.getIngridient());
                data.put("howToCook", recipesDTO.getHowToCook());

                // Menggunakan metode findByUserIdAndRecipeId untuk mendapatkan FavoriteFoods
                //Optional<Boolean> isFavoriteOpt = favoriteFoodsService.findIsFavoriteByUserIdAndRecipeId(1, recipesDTO.getRecipeId());

                boolean isFavorite = getFavFood(userId,recipesDTO.getRecipeId());


                // Menambahkan isFavorite ke dalam data
                data.put("isFavorite", isFavorite);
                response.put("total", 1);
                response.put("data", data);
                response.put("message", "Recipe found successfully");
                response.put("statusCode", 200);
                response.put("status", "success");
            } else {
                response.put("total", 0);
                response.put("data", null);
                response.put("message", "Recipe not found");
                response.put("statusCode", 404);
                response.put("status", "error");
            }
        } catch (Exception e) {
            // Tangani exception sesuai kebutuhan Anda
            response.put("total", 0);
            response.put("data", null);
            response.put("message", "Error occurred while processing the request");
            response.put("statusCode", 500);
            response.put("status", "error");
        }

        return response;
    }
}
