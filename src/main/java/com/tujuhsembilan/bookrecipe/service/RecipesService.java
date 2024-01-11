package com.tujuhsembilan.bookrecipe.service;

import java.io.IOException;
import java.io.InputStream;

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
import com.tujuhsembilan.bookrecipe.model.Categories;
import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;
import com.tujuhsembilan.bookrecipe.model.Levels;
import com.tujuhsembilan.bookrecipe.model.Recipes;
import com.tujuhsembilan.bookrecipe.repository.FavoriteFoodsRepository;
import com.tujuhsembilan.bookrecipe.repository.RecipesRepository;
import com.tujuhsembilan.bookrecipe.security.service.UserDetailsImplement;
import com.tujuhsembilan.bookrecipe.service.spesification.FavoriteFoodSpecification;
import com.tujuhsembilan.bookrecipe.service.spesification.RecipeSpesification;
import com.tujuhsembilan.bookrecipe.service.spesification.filter.RecipeFilter;
import lib.minio.MinioSrvc;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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




	@Autowired
    private RecipesRepository recipeRepo;

	@Autowired
	private FavoriteFoodsRepository favoriteRepo;

	@Lazy
	@Autowired
	private MinioSrvc minioService;

	private final String bucket = "talent79-dev";

	public ResponseEntity<Object> getResepSaya(MyRecipeRequestDTO myRecipesDTO, String sortBy, int pageSize,
			int pageNumber) {

		try {
			Sort sortByNameAsc = Sort.by(Sort.Direction.ASC, "recipeName");
			Sort sortByNameDesc = Sort.by(Sort.Direction.DESC, "recipeName");
			Sort sortByTimeAsc = Sort.by(Sort.Direction.ASC, "timeCook");
			Sort sortByTimeDesc = Sort.by(Sort.Direction.DESC, "timeCook");

			int newPage = pageSize - 1;

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

			PageRequest pageRequest = PageRequest.of(newPage, pageNumber, choosenSort);

			Specification<Recipes> recipeSpec = RecipeSpesification.recipeFilter(myRecipesDTO);

			Page<Recipes> recipes = recipeRepo.findAll(recipeSpec, pageRequest);
			List<MyRecipeResDTO> response = recipes.stream().map(recipe -> new MyRecipeResDTO(
					recipe.getRecipeId(),
					new MyRecipeCategoriesDTO(recipe.getCategories().getCategoryId(),
							recipe.getCategories().getCategoryName()),
					new MyRecipesLevelsDTO(recipe.getLevels().getLevelId(), recipe.getLevels().getLevelName()),
					recipe.getRecipeName(),
					getImageURL(bucket, recipe.getImageFilename()),
					recipe.getTimeCook(),
					getFavFood(recipe.getRecipeId(), recipe.getUsers().getUserId())))
					.collect(Collectors.toList());

			long totalData = recipeRepo.count(recipeSpec);

			Map<String, Object> result = new LinkedHashMap<String, Object>();

			result.put("total", totalData);
			result.put("data", response);
			result.put("message", "Berhasil memuat Resep Masakan Saya");

			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			System.err.println("An error occurred: " + e.getMessage());

			// Create a response for the error
			Map<String, Object> errorResult = new LinkedHashMap<String, Object>();
			errorResult.put("message", "An internal server error occurred");

			return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private Boolean getFavFood(Integer recipeId, Integer userId) {
		FavoriteFoods favFood = favoriteRepo.findById_RecipeIdAndId_UserId(recipeId, userId).orElse(null);
		Boolean isFavorite = false;

		if (favFood != null) {
			return favFood.getIsFavorite();
		}

		return isFavorite;
	}

	private String getImageURL(String bucket, String filename) {
		String url = "";

		if(bucket != null && filename != null) {
			url = minioService.getPublicLink(bucket, filename);
		}

		return url;
	}

	public ResponseEntity<Object> deleteResepSaya(int recipeId, int userId) {
		try {
			Recipes resepSaya = recipeRepo.findByRecipeIdAndUsers_UserId(recipeId, userId).orElse(null);

			String message = "";
			Integer jumlahResepDihapus = 0;

			if (resepSaya != null) {
				jumlahResepDihapus = 1;
				resepSaya.setIsDeleted(true);
				recipeRepo.save(resepSaya);
				message = "Resep " + resepSaya.getRecipeName() + " berhasil dihapus";
			} else {
				message = "Resep tidak ditemukan!";
			}

			Map<String, Object> result = new LinkedHashMap<String, Object>();

			result.put("total", jumlahResepDihapus);
			result.put("data", "");
			result.put("message", message);

			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			System.err.println("An error occurred: " + e.getMessage());

			// Create a response for the error
			Map<String, Object> errorResult = new LinkedHashMap<String, Object>();
			errorResult.put("message", "An internal server error occurred");

			return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

    public Object getDataByIdWithFilterAndSort(int page, int pageSize, RecipeFilter filter) {
        DisplayPaginationRecipeFav response = new DisplayPaginationRecipeFav();
        try {
//            UserDetailsImplement userDetails = (UserDetailsImplement) SecurityContextHolder
//                    .getContext()
//                    .getAuthentication()
//                    .getPrincipal();
//            log.info("Read Recipes with User id " + userDetails.getId() + " Success!");

            FavoriteFoodSpecification specification = new FavoriteFoodSpecification(filter);

            Page<FavoriteFoods> favoriteFoodsPage = favoriteRepo.findAll(
                    specification,
                    PageRequest.of(page, pageSize, specification.getSort())
            );



            List<UserFav> userFavList = favoriteFoodsPage.getContent().stream()
//                    .filter(fav -> fav.getId().getUserId() == userDetails.getId() )
                    .filter(favActive -> favActive.getIsFavorite())
                    .map(this::mapFavoriteFoodsToUserFav)
                    .collect(Collectors.toList());


            if (userFavList.isEmpty()) {
                return new ErrorDTO(HttpStatus.NOT_FOUND.value(), "Data Not Found",
                        "User Not Found");
            }
            response.setTotal(userFavList.size());
            response.setData(userFavList);
            response.setMessage("Success"); // todo use message.properties
            response.setStatus("Success Get Data");
            response.setStatusCode(HttpStatus.OK.value());

        } catch (DataAccessException e) {
            return new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Data Access Error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected Error",
                    "cause :\n"+ e.getCause() +"\n " + e.getMessage() );
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
                        imageUrl = minioService.getLink(bucket, recipe.getImageFilename(), MinioSrvc.DEFAULT_EXPIRY);
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
}
