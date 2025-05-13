package com.univesp.projeto_integrador.dto;

import com.univesp.projeto_integrador.model.Product;
import jakarta.annotation.Nullable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductRequest(
        @NotBlank @Size(max = 100) String productName,
        @Size(max = 50) String productType,
        @Min(1) int quantity,
        @NotBlank @Size(max = 50) String batchNumber,
        String description,
        @NotNull @Future LocalDate expirationDate,
        @NotNull @DecimalMin("0.00") @Digits(integer = 3, fraction = 2) BigDecimal profitMargin,
        @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal batchPrice,
        @Nullable
        Long promotionId,
        @Enumerated(EnumType.STRING)
        Product.ProductStatus status
) {}