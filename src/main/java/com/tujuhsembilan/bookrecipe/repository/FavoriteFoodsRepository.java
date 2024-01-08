package com.tujuhsembilan.bookrecipe.repository;

import com.tujuhsembilan.bookrecipe.model.FavoriteFoods;
import com.tujuhsembilan.bookrecipe.model.FavoriteFoodsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FavoriteFoodsRepository extends JpaRepository<FavoriteFoods, FavoriteFoodsId>,
        JpaSpecificationExecutor<FavoriteFoods> {
}
