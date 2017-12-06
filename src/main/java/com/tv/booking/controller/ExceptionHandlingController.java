package com.tv.booking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tv.booking.exception.AlreadyReservedException;
import com.tv.booking.exception.ExceedSeatAvailabilityException;
import com.tv.booking.exception.HoldExpiredException;
import com.tv.booking.exception.InvalidSeatHoldException;
import com.tv.booking.servicedto.ExceptionResponse;

@ControllerAdvice
public class ExceptionHandlingController {
 
    @ExceptionHandler(value = {ExceedSeatAvailabilityException.class, 
    		HoldExpiredException.class, InvalidSeatHoldException.class,
    		AlreadyReservedException.class, IllegalArgumentException.class})
    public ResponseEntity<ExceptionResponse> badRequest(RuntimeException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Bad request");
        response.setErrorMessage(ex.getMessage());
 
        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }
}

