package com.univesp.projeto_integrador.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PromotionResponse(
        Long id,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal discountPercentage,
        String status
) {}