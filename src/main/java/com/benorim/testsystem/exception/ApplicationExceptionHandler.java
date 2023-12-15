package com.benorim.testsystem.exception;

import com.benorim.testsystem.controller.api.response.ErrorResponse;
import com.benorim.testsystem.enums.ErrorState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(InvalidOptionsException.class)
    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidOptionsException(InvalidOptionsException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage(), ErrorState.INVALID_OPTIONS), HttpStatus.BAD_REQUEST);
    }
}
