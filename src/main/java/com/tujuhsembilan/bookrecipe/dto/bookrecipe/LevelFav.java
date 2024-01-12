package com.tujuhsembilan.bookrecipe.dto.bookrecipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LevelFav {
    private int levelId;
    private String levelName;
}
