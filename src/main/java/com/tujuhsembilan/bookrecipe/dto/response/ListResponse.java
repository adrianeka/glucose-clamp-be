package com.tujuhsembilan.bookrecipe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListResponse<T> {
    private List<T> data;
    private String message;
    private int statusCode;
    private String status;
}
