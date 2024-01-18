package com.tujuhsembilan.bookrecipe.service.specification;

import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;
import com.tujuhsembilan.bookrecipe.service.specification.filter.RecipeFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFoodSpecification implements Specification<FavoriteFoods> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1763760009680597946L;
	private RecipeFilter filter;

    public FavoriteFoodSpecification(RecipeFilter filter) {
        this.filter = filter;
    }

    @Override
    public Specification<FavoriteFoods> and(Specification<FavoriteFoods> other) {
        return Specification.super.and(other);
    }

    @Override
    public Specification<FavoriteFoods> or(Specification<FavoriteFoods> other) {
        return Specification.super.or(other);
    }

    @Override
    public Predicate toPredicate(Root<FavoriteFoods> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Boolean isFavorite = true;
        predicates.add(criteriaBuilder.equal(root.get("users").get("userId"), filter.getUserId()));
        predicates.add(criteriaBuilder.equal(root.get("isFavorite"), isFavorite));


        if (filter.getRecipeName() != null) {
            predicates.add(criteriaBuilder.equal(root.get("recipes").get("recipeName"), filter.getRecipeName()));
        }

        if (filter.getLevel() != null) {
            predicates.add(criteriaBuilder.equal(root.get("recipes").get("levels").get("levelName"), filter.getLevel()));
        }

        if (filter.getCategory() != null) {
            predicates.add(criteriaBuilder.equal(root.get("recipes").get("categories").get("categoryName"), filter.getCategory()));
        }

        if (filter.getCookMin() != null && filter.getCookMax() != null) {
            if (filter.getCookMin() != 0 || filter.getCookMax() != 0) {
                predicates.add(criteriaBuilder.between(root.get("recipes").get("timeCook"),
                        filter.getCookMin(), filter.getCookMax()));
            }
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    public Sort getSort() {
        final int NAME_RECIPE_ASC = 1;
        final int NAME_RECIPE_DESC = 2;

        if (filter.getSort() == null) {
            return Sort.by(Sort.Order.asc("recipes.recipeName"));
        }

        return switch (filter.getSort()) {
            case NAME_RECIPE_ASC -> Sort.by(Sort.Order.asc("recipes.recipeName"));
            case NAME_RECIPE_DESC -> Sort.by(Sort.Order.desc("recipes.recipeName"));
            default -> Sort.by(Sort.Order.asc("recipes.recipeName"));
        };
    }
}
