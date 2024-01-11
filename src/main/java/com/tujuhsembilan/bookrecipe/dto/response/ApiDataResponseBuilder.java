package com.tujuhsembilan.bookrecipe.dto.response;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiDataResponseBuilder {
    private Object data;
    private String message;
    private int statusCode;
    private HttpStatus status;
    
    @Builder
    public ApiDataResponseBuilder(Object data, String message, int statusCode, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.statusCode = statusCode;
        this.status = status;
    }
}
