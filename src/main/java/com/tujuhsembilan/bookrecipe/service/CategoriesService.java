package com.tujuhsembilan.bookrecipe.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tujuhsembilan.bookrecipe.model.Categories;
import com.tujuhsembilan.bookrecipe.repository.CategoriesRepository;

@Service
public class CategoriesService {
    @Autowired
    private CategoriesRepository categoriesRepository;

    public List<Categories> getAllCategories() {
        return categoriesRepository.findAll();
    }
}
