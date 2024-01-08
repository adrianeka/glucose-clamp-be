package com.tujuhsembilan.bookrecipe.service.spesification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.tujuhsembilan.bookrecipe.dto.RecipesDTO;

import jakarta.persistence.criteria.Predicate;

public class RecipeSpesification {
	public static Specification<RecipesDTO> recipeFilter(String recipeName){
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			if (recipeName != null) {
				String recipeNameValue = "%" + recipeName + "%";
				Predicate recipeNamePredicates = criteriaBuilder.like(root.get("recipeName"), recipeNameValue);
				predicates.add(recipeNamePredicates);
			}
			
			return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
}
