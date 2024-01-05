package com.tujuhsembilan.bookrecipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tujuhsembilan.bookrecipe.model.Categories;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Integer> {

    
}