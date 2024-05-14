package com.tujuhsembilan.bookrecipe.service.specification;

import com.tujuhsembilan.bookrecipe.dto.request.RecipeFilterDTO;
import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFoodSpesification {
    public static Specification<FavoriteFoods> recipesSpecification(RecipeFilterDTO myRecipeDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();

            predicates.add(criteriaBuilder.equal(root.get("id").get("userId"), myRecipeDTO.getUserId()));
            predicates.add(criteriaBuilder.equal(root.get("isFavorite"), true));
            predicates.add(criteriaBuilder.equal(root.get("recipes").get("isDeleted"), false));

            if (myRecipeDTO.getRecipeName() != null) {
                String recipeNameValue = "%" + myRecipeDTO.getRecipeName().toLowerCase() + "%";
                Predicate recipeNamePredicates = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("recipes").get("recipeName")), recipeNameValue);
                predicates.add(recipeNamePredicates);
            }

            if (myRecipeDTO.getLevelId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("recipes").get("levels").get("levelId"),
                        myRecipeDTO.getLevelId()));
            }

            if (myRecipeDTO.getCategoryId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("recipes").get("categories").get("categoryId"),
                        myRecipeDTO.getCategoryId()));
            }

            if (myRecipeDTO.getTime() != null) {
                if (myRecipeDTO.getTime() <= 30) {
                    predicates.add(criteriaBuilder.between(root.get("recipes").get("timeCook"), 0, 30));
                } else if (myRecipeDTO.getTime() > 30 && myRecipeDTO.getTime() <= 60) {
                    predicates.add(criteriaBuilder.between(root.get("recipes").get("timeCook"), 31, 60));
                } else {
                    predicates.add(criteriaBuilder.greaterThan(root.get("recipes").get("timeCook"), 60));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
