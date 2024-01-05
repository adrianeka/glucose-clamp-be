package com.tujuhsembilan.bookrecipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;
import com.tujuhsembilan.bookrecipe.model.FavoriteFoodsId;

public interface FavoriteFoodsRepository extends JpaRepository<FavoriteFoods, FavoriteFoodsId>{
    
}
