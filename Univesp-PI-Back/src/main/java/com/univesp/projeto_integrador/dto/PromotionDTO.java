package com.univesp.projeto_integrador.dto;
//Refazer depois

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.univesp.projeto_integrador.model.PromotionStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PromotionDTO {

    private Long promotionId;

    @NotBlank(message = "A descrição da promoção é obrigatória.")
    @Size(max = 255, message = "A descrição da promoção pode ter no máximo 255 caracteres.")
    private String promotionDescription;

    @NotNull(message = "A data de início da promoção é obrigatória.")
    @FutureOrPresent(message = "A data de início deve ser no presente ou no futuro.")
    private LocalDate startDate;

    @NotNull(message = "A data de término da promoção é obrigatória.")
    @Future(message = "A data de término deve ser uma data futura.")
    private LocalDate endDate;

    @NotNull(message = "O percentual de desconto é obrigatório.")
    @DecimalMin(value = "0.00", message = "O percentual de desconto deve ser no mínimo 0%.")
    @DecimalMax(value = "100.00", message = "O percentual de desconto não pode exceder 100%.")
    @Digits(integer = 3, fraction = 2, message = "O percentual de desconto deve ter no máximo 3 dígitos inteiros e 2 decimais.")
    private BigDecimal discountPercentage;

    @NotNull(message = "O status da promoção é obrigatório.")
    private PromotionStatus status;
}
