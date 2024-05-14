package com.tujuhsembilan.bookrecipe.service.specification;

import com.tujuhsembilan.bookrecipe.dto.request.RecipeFilterRequestDTO;
import com.tujuhsembilan.bookrecipe.model.Recipes;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RecipeListSpecification {
    public static Specification<Recipes> recipesFilterAll(RecipeFilterRequestDTO recipeFiltersDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();

            Predicate isDeletedPredicate = criteriaBuilder.equal(root.get("isDeleted"), false);
            predicates.add(isDeletedPredicate);

            if (recipeFiltersDTO.getRecipeName() != null) {
                String recipeNameValue = "%" + recipeFiltersDTO.getRecipeName().toLowerCase() + "%";
                Predicate recipeNamePredicates = criteriaBuilder.like(criteriaBuilder.lower(
                        root.get("recipeName")),
                        recipeNameValue);
                predicates.add(recipeNamePredicates);
            }

            if (recipeFiltersDTO.getLevelId() != null) {
                Predicate levelPredicate = criteriaBuilder.equal(
                        root.get("levels").get("levelId"), recipeFiltersDTO.getLevelId());
                predicates.add(levelPredicate);
            }

            if (recipeFiltersDTO.getCategoryId() != null) {
                Predicate categoryPredicate = criteriaBuilder.equal(
                        root.get("categories").get("categoryId"),
                        recipeFiltersDTO.getCategoryId());
                predicates.add(categoryPredicate);
            }

            if (recipeFiltersDTO.getTime() != null) {
                String time = recipeFiltersDTO.getTime();
                if (time.equals("0-30")) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timeCook"), 30));
                } else if (time.equals("30-60")) {
                    predicates.add(criteriaBuilder.between(root.get("timeCook"), 31, 60));
                } else if (time.equals("60")) {
                    predicates.add(criteriaBuilder.greaterThan(root.get("timeCook"), 60));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));

        };
    }
}
