package com.tujuhsembilan.bookrecipe.service;

import com.tujuhsembilan.bookrecipe.dto.CategoriesDTO;
import com.tujuhsembilan.bookrecipe.dto.LevelsDTO;
import com.tujuhsembilan.bookrecipe.dto.RecipesDTO;
import com.tujuhsembilan.bookrecipe.dto.bookrecipe.CategoryFav;
import com.tujuhsembilan.bookrecipe.dto.bookrecipe.DisplayPaginationRecipeFav;
import com.tujuhsembilan.bookrecipe.dto.bookrecipe.LevelFav;
import com.tujuhsembilan.bookrecipe.dto.bookrecipe.UserFav;
import com.tujuhsembilan.bookrecipe.dto.request.CreateRecipeRequest;
import com.tujuhsembilan.bookrecipe.dto.request.MyRecipeRequestDTO;
import com.tujuhsembilan.bookrecipe.dto.request.RecipeFilterDTO;
import com.tujuhsembilan.bookrecipe.dto.request.UpdateRecipeRequest;
import com.tujuhsembilan.bookrecipe.dto.response.*;
import com.tujuhsembilan.bookrecipe.exception.classes.Exception;
import com.tujuhsembilan.bookrecipe.exception.classes.*;
import com.tujuhsembilan.bookrecipe.model.*;
import com.tujuhsembilan.bookrecipe.repository.*;
import com.tujuhsembilan.bookrecipe.security.service.UserDetailsImplement;
import com.tujuhsembilan.bookrecipe.service.specification.FavoriteFoodSpesification;
import com.tujuhsembilan.bookrecipe.service.specification.RecipeSpesification;
import jakarta.persistence.EntityNotFoundException;
import lib.i18n.utility.MessageUtil;
import lib.minio.MinioSrvc;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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
    private ModelMapper modelMapper;

    @Autowired
    private FavoriteFoodsRepository favoriteRepo;

    @Lazy
    @Autowired
    private MinioSrvc minioService;

    @Autowired
    private MessageUtil messageUtil;

    @Transactional
    public MessageResponse create(CreateRecipeRequest request, MultipartFile imageFile, int userId) {
        validationService.validate(request);

        Users createdByUser = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageUtil.get("application.error.user.not-found", userId)));

        Categories categories = categoriesRepository.findById(request.getCategories().getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        messageUtil.get("application.error.category.not-found",
                                request.getCategories().getCategoryId())));

        Levels levels = levelsRepository.findById(request.getLevels().getLevelId())
                .orElseThrow(() -> new EntityNotFoundException(
                        messageUtil.get("application.error.level.not-found", request.getLevels().getLevelId())));

        String imageFilename;
        try {
            imageFilename = minioService.uploadImageToMinio(request, imageFile);
        } catch (IOException e) {
            String errorMessage = messageUtil.get("application.error.upload.minio");
            log.error(errorMessage, e);
            throw new MinioUploadException(errorMessage, e);
        }

        log.info(imageFilename);

        Recipes newRecipe = Recipes.builder()
                .users(createdByUser)
                .categories(categories)
                .levels(levels)
                .recipeName(request.getRecipeName())
                .imageFilename(imageFilename)
                .timeCook(request.getTimeCook())
                .ingredient(request.getIngredient())
                .howToCook(request.getHowToCook())
                .createdBy(createdByUser.getUsername())
                .modifiedBy(createdByUser.getUsername())
                .isDeleted(false)
                .createdTime(new Timestamp(System.currentTimeMillis()))
                .modifiedTime(new Timestamp(System.currentTimeMillis()))
                .build();

        recipesRepository.save(newRecipe);

        String responseMessage = messageUtil.get("application.success.add.resep", request.getRecipeName());
        int statusCode = HttpStatus.OK.value();
        String status = HttpStatus.OK.getReasonPhrase();

        log.info(responseMessage, statusCode, status);

        return new MessageResponse(responseMessage, statusCode, status);
    }

    @Transactional
    public MessageResponse updateRecipeById(UpdateRecipeRequest request, MultipartFile imageFile, int userId) {
        validationService.validate(request);

        Users modifiedByUser = usersRepository.findById(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException(messageUtil.get("application.error.user.not-found", userId)));

        Recipes existingRecipe = recipesRepository.findById(request.getRecipeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        messageUtil.get("application.error.data-not-found", request.getRecipeId())));

        Categories categories = categoriesRepository.findById(request.getCategories().getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        messageUtil.get("application.error.category.not-found",
                                request.getCategories().getCategoryId())));

        Levels levels = levelsRepository.findById(request.getLevels().getLevelId())
                .orElseThrow(() -> new EntityNotFoundException(
                        messageUtil.get("application.error.level.not-found", request.getLevels().getLevelId())));

        Recipes existingUser = recipesRepository.findById(request.getRecipeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        messageUtil.get("application.error.data-not-found", request.getRecipeId())));

        // Verifikasi bahwa userId sesuai dengan userId di resep
        if (existingUser.getUsers().getUserId() != userId) {
            // UserId tidak sesuai, kembalikan pesan kesalahan atau lempar pengecualian
            return new MessageResponse(messageUtil.get("application.error.recipe.access-forbidden"),
                    HttpStatus.FORBIDDEN.value(),
                    HttpStatus.FORBIDDEN.getReasonPhrase());
        }

        existingRecipe.setCategories(categories);
        existingRecipe.setLevels(levels);
        existingRecipe.setRecipeName(request.getRecipeName());
        existingRecipe.setTimeCook(request.getTimeCook());
        existingRecipe.setIngredient(request.getIngredient());
        existingRecipe.setHowToCook(request.getHowToCook());
        existingRecipe.setModifiedBy(modifiedByUser.getUsername());
        existingRecipe.setModifiedTime(new Timestamp(System.currentTimeMillis()));

        if (imageFile != null) {
            try {
                String newImageFilename = minioService.updateImageToMinio(request, imageFile);
                existingRecipe.setImageFilename(newImageFilename);
            } catch (IOException e) {
                String errorMessage = messageUtil.get("application.error.upload.minio");
                log.error(errorMessage, e);
                throw new MinioUploadException(errorMessage, e);
            }
        }

        recipesRepository.save(existingRecipe);

        String responseMessage = messageUtil.get("application.success.update.resep", request.getRecipeName());
        int statusCode = HttpStatus.OK.value();
        String status = HttpStatus.OK.getReasonPhrase();

        log.info(responseMessage, statusCode, status);

        return new MessageResponse(responseMessage, statusCode, status);
    }

    public ResponseEntity<Object> getResepSaya(MyRecipeRequestDTO myRecipesDTO, Pageable page) {

        Specification<Recipes> recipeSpec = RecipeSpesification.recipeFilter(myRecipesDTO);

        Page<Recipes> recipes = recipesRepository.findAll(recipeSpec, page);

        if (recipes.isEmpty()) {
            throw new DataNotFoundException(messageUtil.get("application.error.data-not-found"));
        } else {
            long totalData = recipesRepository.count(recipeSpec);
            List<MyRecipeResDTO> response = recipes.stream().map(recipe -> new MyRecipeResDTO(
                    recipe.getRecipeId(),
                    new MyRecipeCategoriesDTO(recipe.getCategories().getCategoryId(),
                            recipe.getCategories().getCategoryName()),
                    new MyRecipesLevelsDTO(recipe.getLevels().getLevelId(), recipe.getLevels().getLevelName()),
                    recipe.getRecipeName(),
                    getImageURL(recipe.getImageFilename()),
                    recipe.getTimeCook(),
                    getFavFood(recipe.getRecipeId(), recipe.getUsers().getUserId())))
                    .collect(Collectors.toList());

            ResponseBodyDTO responseBody = ResponseBodyDTO.builder()
                    .total(totalData)
                    .data(response)
                    .message(messageUtil.get("application.success.load", "Resep Saya"))
                    .statusCode(HttpStatus.OK.value())
                    .status(HttpStatus.OK.name())
                    .build();

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }

    }

    private boolean getFavFood(int recipeId, int userId) {
        Optional<Boolean> favFood = favoriteRepo.findIsFavorite(userId, recipeId);

        if (favFood.isPresent()) {
            return favFood.get();
        } else {
            return false;
        }
    }

    private String getImageURL(String filename) {
        String url = "";

        if (filename != null) {
            url = minioService.getPublicLink(filename);
        }

        return url;
    }

    public ResponseEntity<Object> deleteResepSaya(int recipeId, int userId) {
        Recipes resepSaya = recipesRepository.findByMyRecipe(recipeId, userId).orElseThrow(
                () -> new DataNotFoundException(messageUtil.get("application.error.not-found.resep-saya")));

        String message = "";
        Integer jumlahResepDihapus = 0;

        if (resepSaya.getIsDeleted()) {
            throw new AlreadyDeletedException(
                    messageUtil.get("application.error.already-deleted.resep", resepSaya.getRecipeName()));
        } else {
            jumlahResepDihapus = 1;
            resepSaya.setIsDeleted(true);
            recipesRepository.save(resepSaya);
            message = messageUtil.get("application.success.delete.resep", resepSaya.getRecipeName());
        }

        ResponseBodyDTO responseBody = ResponseBodyDTO.builder()
                .total(jumlahResepDihapus)
                .message(message)
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .build();

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    public Object getDataByIdWithFilterAndSort(RecipeFilterDTO filter, Pageable page) {
        try {
            Sort sort = page.getSort();
            List<Sort.Order> orders = new ArrayList<>();

            sort.forEach(order -> {
                String property = order.getProperty();
                String prefixedProperty = "recipes." + property; // Add your prefix here
                Sort.Order newOrder = new Sort.Order(order.getDirection(), prefixedProperty);
                orders.add(newOrder);
            });

            Sort newSort = Sort.by(orders);

            PageRequest request = PageRequest.of(page.getPageNumber(), page.getPageSize(), newSort);

            Specification<FavoriteFoods> recipeSpec = FavoriteFoodSpesification.recipesSpecification(filter);
            Page<FavoriteFoods> favoriteFoods = favoriteRepo.findAll(recipeSpec, request);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetailsImplement) {
                List<UserFav> userFavList = favoriteFoods.getContent().stream()
                        .map(this::mapFavoriteFoodsToUserFav)
                        .collect(Collectors.toList());

                if (userFavList.isEmpty()) {
                    throw new DataNotFoundException(messageUtil.get("application.error.data-not-found"));
                }

                return DisplayPaginationRecipeFav.builder()
                        .total(favoriteRepo.count(recipeSpec))
                        .data(userFavList)
                        .message(messageUtil.get("application.success.load", "Resep Masakan Favorit"))
                        .status(HttpStatus.OK.name())
                        .statusCode(HttpStatus.OK.value())
                        .build();
            } else if (principal instanceof String) {
                throw new UnauthorizedUserException(messageUtil.get("application.error.unauthorized-user.detail"));
            }

        } catch (DataAccessException e) {
            throw new DataAccessException(messageUtil.get("application.error.data-access"));
        }

        // Default throw if none of the conditions are met
        throw new UnknownAuthenticationException(messageUtil.get("application.error.unknown.principal.type.detail"));
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
                        imageUrl = minioService.getLink(recipe.getImageFilename(), MinioSrvc.DEFAULT_EXPIRY);
                    } catch (Exception e) {
                        log.error(messageUtil.get("application.error.image-url.minio", recipe.getRecipeId()), e);
                    }
                    userFav.setImageUrl(imageUrl);
                    userFav.setTime(recipe.getTimeCook());
                    userFav.setIsFavorite(favoriteFoods.getIsFavorite());
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
                        log.error(messageUtil.get("application.error.category-or-level.null", recipe.getRecipeId()));
                    }
                    return userFav;
                })
                .orElseGet(() -> {
                    log.error(messageUtil.get("application.error.data-not-found"));
                    return new UserFav();
                });
    }

    public ResponseBodyDTO getRecipeById(int recipeId) {
        ResponseBodyDTO response = new ResponseBodyDTO();

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();
            int userId = 1;

            if (principal instanceof UserDetailsImplement) {
                UserDetailsImplement userDetails = (UserDetailsImplement) principal;
                userId = userDetails.getId();

            }

            Optional<Recipes> recipeOptional = recipesRepository.findById(recipeId);

            if (recipeOptional.isPresent() && recipeOptional.get().getIsDeleted() == false) {
                Recipes recipe = recipeOptional.get();
                RecipesDTO recipesDTO = modelMapper.map(recipe, RecipesDTO.class);
                RecipesDTO dataRecipe = new RecipesDTO();

                dataRecipe.setRecipeId(recipesDTO.getRecipeId());

                CategoriesDTO categoriesDTO = recipesDTO.getCategories();
                dataRecipe.setCategories(categoriesDTO);

                LevelsDTO levelDTO = recipesDTO.getLevels();
                dataRecipe.setLevels(levelDTO);

                dataRecipe.setRecipeName(recipesDTO.getRecipeName());
                dataRecipe.setImageFilename(getImageURL(recipesDTO.getImageFilename()));
                dataRecipe.setTimeCook(recipesDTO.getTimeCook());
                dataRecipe.setIngredient(recipesDTO.getIngredient());
                dataRecipe.setHowToCook(recipesDTO.getHowToCook());
                dataRecipe.setIsFavorite(getFavFood(recipesDTO.getRecipeId(), userId));

                response.setTotal(1);
                response.setData(dataRecipe);
                response.setMessage(messageUtil.get("application.success.recipe.found"));
                response.setStatusCode(200);
                response.setStatus("success");
            } else {
                response.setTotal(0);
                response.setData(null);
                response.setMessage(messageUtil.get("application.error.data-not-found"));
                response.setStatusCode(404);
                response.setStatus("error");
            }
        } catch (Exception e) {
            log.error("Exception occurred while processing the request", e);
            throw new Exception(messageUtil.get("application.error.internal"));
        }

        return response;
    }

}
