package com.tujuhsembilan.bookrecipe.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ListResponse<T> {
    private List<T> data;
    private String message;
    private int statusCode;
    private String status;
}
