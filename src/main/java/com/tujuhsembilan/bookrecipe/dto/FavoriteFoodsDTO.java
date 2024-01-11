package com.tujuhsembilan.bookrecipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteFoodsDTO {
    private FavoriteFoodsIdDTO id;
    private RecipesDTO recipes;
    private UsersDTO users;
}
