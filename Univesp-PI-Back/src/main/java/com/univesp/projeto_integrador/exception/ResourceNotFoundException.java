package com.univesp.projeto_integrador.exception;
//Refazer depois

public class ResourceNotFoundException extends RuntimeException {
    // Construtor correto
    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " não encontrado(a) com ID: " + id);
    }
}
