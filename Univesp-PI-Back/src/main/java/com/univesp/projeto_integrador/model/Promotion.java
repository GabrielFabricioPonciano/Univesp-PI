package com.univesp.projeto_integrador.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
//Refazer depois

@Entity
@Table(name = "promotions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promotionId;

    // Descrição da promoção - não pode ser nula e pode ter no máximo 255 caracteres
    @NotBlank(message = "A descrição da promoção é obrigatória.")
    @Size(max = 255, message = "A descrição da promoção pode ter no máximo 255 caracteres.")
    @Column(length = 255)
    private String promotionDescription;

    // Data de início - não pode ser nula e deve ser uma data futura ou presente
    @NotNull(message = "A data de início da promoção é obrigatória.")
    @FutureOrPresent(message = "A data de início deve ser no presente ou no futuro.")
    @Column(name = "start_date")
    private LocalDate startDate;

    // Data de término - não pode ser nula e deve ser uma data no futuro
    @NotNull(message = "A data de término da promoção é obrigatória.")
    @Future(message = "A data de término deve ser uma data futura.")
    @Column(name = "end_date")
    private LocalDate endDate;

    // Percentual de desconto - valor obrigatório entre 0% e 100%, com no máximo 2 casas decimais
    @NotNull(message = "O percentual de desconto é obrigatório.")
    @DecimalMin(value = "0.00", message = "O percentual de desconto deve ser no mínimo 0%.")
    @DecimalMax(value = "100.00", message = "O percentual de desconto não pode exceder 100%.")
    @Digits(integer = 3, fraction = 2, message = "O percentual de desconto deve ter no máximo 3 dígitos inteiros e 2 decimais.")
    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    // Status da promoção - campo obrigatório com valores específicos
    @NotNull(message = "O status da promoção é obrigatório.")
    @Enumerated(EnumType.STRING)
    private PromotionStatus status = PromotionStatus.ACTIVE;

    // Lista de produtos - não pode ser nula
    @NotNull(message = "A lista de produtos não pode ser nula.")
    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

}
