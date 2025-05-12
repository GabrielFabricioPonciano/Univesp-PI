package com.univesp.projeto_integrador.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProductResponse(
        Long productId,
        String productName,
        String productType,
        int quantity,
        String batchNumber,
        String description,
        LocalDate expirationDate,
        BigDecimal profitMargin,
        BigDecimal batchPrice,
        BigDecimal unitPrice,
        BigDecimal finalPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String status,
        Long promotionId
) {}