package com.lorecodex.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();

        // Determinar el estado y el mensaje apropiados según el tipo de excepción
        if (ex instanceof BadCredentialsException) {
            body.put("message", "Invalid username or password");
            body.put("status", HttpStatus.UNAUTHORIZED.value());
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
        } else if (ex instanceof AccessDeniedException) {
            body.put("message", "You don't have permission to access this resource");
            body.put("status", HttpStatus.FORBIDDEN.value());
            return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        } else if (ex instanceof IllegalArgumentException) {
            body.put("message", ex.getMessage());
            body.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        // Controlador predeterminado para otras excepciones
        body.put("message", "An unexpected error occurred");
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        // Registra la excepción para depuración, pero no la expone al cliente
        ex.printStackTrace();

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}