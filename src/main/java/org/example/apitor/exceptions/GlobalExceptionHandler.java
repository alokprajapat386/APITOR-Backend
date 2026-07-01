package org.example.apitor.exceptions;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.ProviderException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(
            ResourceNotFoundException ex
    ){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(
            BadCredentialsException ex
    ){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArguments(
            IllegalArgumentException ex
    ){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<?> handleDuplicateKey(
            DuplicateKeyException ex
    ){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(ProviderException.class)
    public ResponseEntity<?> handleProvide(
            ProviderException ex
    ){
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<?> handleAuthenticationService(
            AuthenticationServiceException ex
    ){
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", ex.getMessage()));
    }


}
