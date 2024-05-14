package com.tujuhsembilan.bookrecipe.service.specification;

import com.tujuhsembilan.bookrecipe.dto.request.MyRecipeRequestDTO;
import com.tujuhsembilan.bookrecipe.model.Recipes;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RecipeSpesification {
	public static Specification<Recipes> recipeFilter(MyRecipeRequestDTO myRecipeDTO) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<Predicate>();

			Predicate idPredicate = criteriaBuilder.equal(root.get("users").get("userId"), myRecipeDTO.getUserId());
			predicates.add(idPredicate);

			Predicate isDeletedPredicate = criteriaBuilder.equal(root.get("isDeleted"), false);
			predicates.add(isDeletedPredicate);

			if (myRecipeDTO.getRecipeName() != null) {
				String recipeNameValue = "%" + myRecipeDTO.getRecipeName().toLowerCase() + "%";
				Predicate recipeNamePredicates = criteriaBuilder.like(criteriaBuilder.lower(root.get("recipeName")),
						recipeNameValue);
				predicates.add(recipeNamePredicates);
			}

			if (myRecipeDTO.getLevelId() != null) {
				Predicate recipeLevelPredicates = criteriaBuilder.equal(root.get("levels").get("levelId"),
						myRecipeDTO.getLevelId());
				predicates.add(recipeLevelPredicates);
			}

			if (myRecipeDTO.getCategoryId() != null) {
				Predicate recipeCategoryPredicates = criteriaBuilder.equal(root.get("categories").get("categoryId"),
						myRecipeDTO.getCategoryId());
				predicates.add(recipeCategoryPredicates);
			}

			if (myRecipeDTO.getTime() != null) {
				if (myRecipeDTO.getTime() <= 30) {
					predicates.add(criteriaBuilder.between(root.get("timeCook"), 0, 30));
				} else if (myRecipeDTO.getTime() > 30 && myRecipeDTO.getTime() <= 60) {
					predicates.add(criteriaBuilder.between(root.get("timeCook"), 31, 60));
				} else {
					predicates.add(criteriaBuilder.greaterThan(root.get("timeCook"), 60));
				}
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
}
