package com.tujuhsembilan.bookrecipe.service;

import com.tujuhsembilan.bookrecipe.dto.ErrorDTO;
import com.tujuhsembilan.bookrecipe.dto.bookrecipe.CategoryFav;
import com.tujuhsembilan.bookrecipe.dto.bookrecipe.DisplayPaginationRecipeFav;
import com.tujuhsembilan.bookrecipe.dto.bookrecipe.LevelFav;
import com.tujuhsembilan.bookrecipe.dto.bookrecipe.UserFav;
import com.tujuhsembilan.bookrecipe.model.Categories;
import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;
import com.tujuhsembilan.bookrecipe.model.Levels;
import com.tujuhsembilan.bookrecipe.repository.FavoriteFoodsRepository;
import com.tujuhsembilan.bookrecipe.security.service.UserDetailsImplement;
import org.springframework.data.domain.Sort;
import com.tujuhsembilan.bookrecipe.spesification.filter.RecipeFilter;
import com.tujuhsembilan.bookrecipe.spesification.spesification.RecipeSpecification;
import lib.minio.MinioSrvc;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tujuhsembilan.bookrecipe.dto.request.MyRecipeRequestDTO;
import com.tujuhsembilan.bookrecipe.dto.response.MyRecipeCategoriesDTO;
import com.tujuhsembilan.bookrecipe.dto.response.MyRecipeResDTO;
import com.tujuhsembilan.bookrecipe.dto.response.MyRecipesLevelsDTO;
import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;
import com.tujuhsembilan.bookrecipe.model.Recipes;
import com.tujuhsembilan.bookrecipe.repository.FavoriteFoodsRepository;
import com.tujuhsembilan.bookrecipe.repository.RecipesRepository;
import com.tujuhsembilan.bookrecipe.service.spesification.RecipeSpesification;

import lib.minio.MinioSrvc;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RecipesService {

	@Autowired
    private RecipesRepository recipeRepo;

	@Autowired
	private FavoriteFoodsRepository favoriteRepo;

	@Autowired
	private MinioSrvc minioService;

	private final String bucket = "talent79-dev";

	public ResponseEntity<Object> getResepSaya(MyRecipeRequestDTO myRecipesDTO, String sortBy, int pageSize, int pageNumber){
		Sort sortByNameAsc = Sort.by(Sort.Direction.ASC, "recipeName");
		Sort sortByNameDesc = Sort.by(Sort.Direction.DESC, "recipeName");
		Sort sortByTimeAsc = Sort.by(Sort.Direction.ASC, "timeCook");
		Sort sortByTimeDesc = Sort.by(Sort.Direction.DESC, "timeCook");

		int newPage = pageSize - 1;

		Sort choosenSort = null;

		boolean isSortByEmpty = (sortBy == null);

		if(!isSortByEmpty) {
			switch(sortBy) {
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
		List<MyRecipeResDTO> response = recipes.stream().map(recipe ->
			new MyRecipeResDTO(
					recipe.getRecipeId(),
					new MyRecipeCategoriesDTO(recipe.getCategories().getCategoryId(), recipe.getCategories().getCategoryName()),
					new MyRecipesLevelsDTO(recipe.getLevels().getLevelId(), recipe.getLevels().getLevelName()),
					recipe.getRecipeName(),
					getImageURL(bucket, recipe.getImageFilename()),
					recipe.getTimeCook(),
					getFavFood(recipe.getRecipeId(), recipe.getUsers().getUserId())
				))
			.collect(Collectors.toList());

		long totalData = recipeRepo.count(recipeSpec);

		Map<String, Object> result = new LinkedHashMap<String, Object>();

		result.put("total", totalData);
		result.put("data", response);
		result.put("message", "Berhasil memuat Resep Masakan Saya");

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	private Boolean getFavFood(Integer recipeId, Integer userId) {
		FavoriteFoods favFood = favoriteRepo.findById_RecipeIdAndId_UserId(recipeId, userId).orElse(null);
		Boolean isFavorite = false;

		if (favFood != null) {
			return favFood.getId().getIsFavorite();
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

	public ResponseEntity<Object> deleteResepSaya(int recipeId, int userId){
		Recipes resepSaya = recipeRepo.findByRecipeIdAndUsers_UserId(recipeId, userId).orElse(null);

		String message = "";
		Integer jumlahResepDihapus = 0;

		if(resepSaya != null) {
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
	}

    public Object getDataByIdWithFilterAndSort(int page, int pageSize, RecipeFilter filter) {
        DisplayPaginationRecipeFav response = new DisplayPaginationRecipeFav();
        try {
//            UserDetailsImplement userDetails = (UserDetailsImplement) SecurityContextHolder
//                    .getContext()
//                    .getAuthentication()
//                    .getPrincipal();
//            log.info("Read Recipes with User id " + userDetails.getId() + " Success!");

            RecipeSpecification specification = new RecipeSpecification(filter);

            Page<FavoriteFoods> favoriteFoodsPage = favoriteFoodsRepository.findAll(
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
                        imageUrl = minioSrvc.getLink(BUCKET_MINIO, recipe.getImageFilename(), MinioSrvc.DEFAULT_EXPIRY);
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
