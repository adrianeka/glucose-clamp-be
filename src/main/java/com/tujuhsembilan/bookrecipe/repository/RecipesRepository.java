package com.tujuhsembilan.bookrecipe.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tujuhsembilan.bookrecipe.dto.RecipesDTO;
import com.tujuhsembilan.bookrecipe.model.Recipes;

public interface RecipesRepository extends JpaRepository<Recipes, Integer>{
    List<RecipesDTO> findByUsers_UserId(int userId, Sort sort, Specification<RecipesDTO> spec);
}
