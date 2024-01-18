package com.tujuhsembilan.bookrecipe.exception.classes;

public class MinioUploadException extends RuntimeException {

    public MinioUploadException(String message) {
        super(message);
    }

    public MinioUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
