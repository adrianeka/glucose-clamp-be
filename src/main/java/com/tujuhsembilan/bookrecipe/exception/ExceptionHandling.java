package com.tujuhsembilan.bookrecipe.exception;

import com.tujuhsembilan.bookrecipe.dto.ErrorDTO;
import com.tujuhsembilan.bookrecipe.exception.classes.AlreadyDeletedException;
import com.tujuhsembilan.bookrecipe.exception.classes.DataAccessException;
import com.tujuhsembilan.bookrecipe.exception.classes.DataNotFoundException;
import com.tujuhsembilan.bookrecipe.exception.classes.MinioUploadException;

import lib.i18n.utility.MessageUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandling {

    @Autowired
    private MessageUtil messageUtil;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleAllExceptions(Exception ex) {
        return new ErrorDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                messageUtil.get("application.error.internal"),
                ex.getMessage()
        );
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleNotFoundExceptions(DataNotFoundException ex) {
        return new ErrorDTO(
                HttpStatus.NOT_FOUND.value(),
                messageUtil.get("application.error.data-not-found"),
                ex.getMessage()
        );
    }
    
    @ExceptionHandler(AlreadyDeletedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleDeletedExceptions(AlreadyDeletedException ex) {
        return new ErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                messageUtil.get("application.error.data-already-deleted"),
                ex.getMessage()
        );
    }
    
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleDataAccessExceptions(DataAccessException ex) {
        return new ErrorDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                messageUtil.get("application.error.data-access"),
                ex.getMessage()
        );
    }

    @ExceptionHandler(MinioUploadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleMinioUploadException(MinioUploadException ex) {
        return new ErrorDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                messageUtil.get("application.error.upload.minio"),
                ex.getMessage()
        );
    }

}
