package com.univesp.projeto_integrador.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PromotionRequest(
        @NotBlank(message = "Descrição obrigatória")
        @Size(max = 255, message = "Máximo 255 caracteres")
        String description,

        @NotNull
        @FutureOrPresent(message = "Data deve ser presente/futura (UTC)")
        LocalDate startDate,

        @NotNull
        @Future(message = "Data deve ser futura (UTC)")
        LocalDate endDate,

        @NotNull(message = "Desconto obrigatório")
        @DecimalMin(value = "0.00", message = "Mínimo 0%")
        @DecimalMax(value = "100.00", message = "Máximo 100%")
        BigDecimal discountPercentage,

        @NotBlank(message = "Status obrigatório")
        @Pattern(regexp = "ACTIVE|INACTIVE|EXPIRED", message = "Status inválido. Use: ACTIVE, INACTIVE ou EXPIRED")
        String status
) {}