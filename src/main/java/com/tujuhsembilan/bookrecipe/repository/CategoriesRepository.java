package com.tujuhsembilan.bookrecipe.repository;

import com.tujuhsembilan.bookrecipe.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
}