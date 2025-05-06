package com.univesp.projeto_integrador.yuxi;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductValidator {

    public void validateBigDecimalField(BigDecimal value, String fieldName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(fieldName + " não pode ser nulo ou menor/igual a zero.");
        }
    }
}
