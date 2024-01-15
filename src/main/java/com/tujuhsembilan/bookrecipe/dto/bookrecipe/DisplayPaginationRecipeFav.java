package com.tujuhsembilan.bookrecipe.dto.bookrecipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisplayPaginationRecipeFav {
    private long total;
    private List<UserFav> data;
    private String message;
    private int statusCode;
    private String status;
}
