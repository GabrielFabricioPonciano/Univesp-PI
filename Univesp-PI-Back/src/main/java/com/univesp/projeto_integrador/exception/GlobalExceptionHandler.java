package com.univesp.projeto_integrador.exception;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
//Refazer depois

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFound(ResourceNotFoundException ex) {
        // Implementar resposta adequada
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Outros handlers de exceção
}
