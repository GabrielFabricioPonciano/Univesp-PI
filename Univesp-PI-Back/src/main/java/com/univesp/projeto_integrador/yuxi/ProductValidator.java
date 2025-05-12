package com.univesp.projeto_integrador.yuxi;

import com.univesp.projeto_integrador.dto.ProductRequest;
import com.univesp.projeto_integrador.exception.InvalidProductDataException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Classe responsável por validar os dados de produtos antes de operações de persistência
 */
@Component
public class ProductValidator {

    /**
     * Valida um objeto ProductRequest completo
     * @param request DTO com dados do produto
     * @throws InvalidProductDataException Se qualquer validação falhar
     */
    public void validateRequest(ProductRequest request) {
        validateStringField(request.productName(), "Nome do produto", 100);
        validateStringField(request.productType(), "Tipo do produto", 50);
        validateStringField(request.batchNumber(), "Número do lote", 50);
        validateQuantity(request.quantity());
        validateDate(request.expirationDate());
        validateProfitMargin(request.profitMargin());
        validatePrice(request.batchPrice());
    }

    /**
     * Valida campos do tipo String
     * @param value Valor do campo
     * @param fieldName Nome do campo para mensagens de erro
     * @param maxLength Tamanho máximo permitido
     */
    private void validateStringField(String value, String fieldName, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new InvalidProductDataException(fieldName + " é obrigatório");
        }
        if (value.length() > maxLength) {
            throw new InvalidProductDataException(
                    fieldName + " deve ter no máximo " + maxLength + " caracteres"
            );
        }
    }

    /**
     * Valida a quantidade em estoque
     * @param quantity Quantidade a ser validada
     */
    private void validateQuantity(int quantity) {
        if (quantity < 1) {
            throw new InvalidProductDataException("A quantidade deve ser maior que zero");
        }
    }

    /**
     * Valida a data de expiração
     * @param date Data a ser validada
     */
    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new InvalidProductDataException("Data de validade é obrigatória");
        }
        if (!date.isAfter(LocalDate.now())) {
            throw new InvalidProductDataException("Data de validade deve ser futura");
        }
    }

    /**
     * Valida a margem de lucro
     * @param margin Porcentagem de ganho
     */
    private void validateProfitMargin(BigDecimal margin) {
        validateBigDecimal(margin, "Margem de lucro");
        if (margin.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new InvalidProductDataException("Margem de lucro não pode exceder 100%");
        }
    }

    /**
     * Valida preço do lote
     * @param price Preço a ser validado
     */
    private void validatePrice(BigDecimal price) {
        validateBigDecimal(price, "Preço do lote");
    }

    /**
     * Validação genérica para campos BigDecimal
     * @param value Valor a ser validado
     * @param fieldName Nome do campo para mensagens de erro
     */
    private void validateBigDecimal(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new InvalidProductDataException(fieldName + " é obrigatório");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidProductDataException(fieldName + " não pode ser negativo");
        }
        if (value.scale() > 2) {
            throw new InvalidProductDataException(
                    fieldName + " deve ter no máximo 2 casas decimais"
            );
        }
    }
}