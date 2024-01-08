package com.tujuhsembilan.bookrecipe.service.spesification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.tujuhsembilan.bookrecipe.dto.request.MyRecipeRequestDTO;
import com.tujuhsembilan.bookrecipe.model.Recipes;

import jakarta.persistence.criteria.Predicate;

public class RecipeSpesification {
	public static Specification<Recipes> recipeFilter(MyRecipeRequestDTO myRecipeDTO){
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			Predicate idPredicate = criteriaBuilder.equal(root.get("users").get("userId"), myRecipeDTO.getUserId());
			predicates.add(idPredicate);
			
			if (myRecipeDTO.getFoodName() != null) {
				String recipeNameValue = "%" + myRecipeDTO.getFoodName() + "%";
				Predicate recipeNamePredicates = criteriaBuilder.like(root.get("recipeName"), recipeNameValue);
				predicates.add(recipeNamePredicates);
			}
			
			return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
}
