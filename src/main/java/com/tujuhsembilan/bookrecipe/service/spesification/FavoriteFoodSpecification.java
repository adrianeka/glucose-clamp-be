package com.tujuhsembilan.bookrecipe.service.spesification;

import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;
import com.tujuhsembilan.bookrecipe.service.spesification.filter.RecipeFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class FavoriteFoodSpecification implements Specification<FavoriteFoods> {

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
            return Sort.by(Sort.Order.asc("recipes.recipeName")); // DEFAULT SORTING
        }

        return switch (filter.getSort()) {
            case NAME_RECIPE_ASC -> Sort.by(Sort.Order.asc("recipes.recipeName"));
            case NAME_RECIPE_DESC -> Sort.by(Sort.Order.desc("recipes.recipeName"));
            default -> Sort.by(Sort.Order.asc("recipes.recipeName")); // DEFAULT SORTING
        };
    }
}
