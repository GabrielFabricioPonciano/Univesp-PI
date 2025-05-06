package com.univesp.projeto_integrador.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
//Refazer depois

@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank(message = "O nome do produto é obrigatório.")
    @Size(max = 100, message = "O nome do produto pode ter no máximo 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String productName;

    @Size(max = 50, message = "O tipo do produto pode ter no máximo 50 caracteres.")
    @Column(length = 50)
    private String productType;

    @Min(value = 1, message = "A quantidade deve ser maior que zero.")
    @Column(nullable = false)
    private int quantity;

    @NotBlank(message = "O número do lote é obrigatório.")
    @Size(max = 50, message = "O número do lote pode ter no máximo 50 caracteres.")
    @Column(nullable = false, length = 50)
    private String numberLote;

    @Lob
    private String description;

    @NotNull(message = "A data de validade é obrigatória.")
    @Future(message = "A data de validade deve ser uma data futura.")
    @Column(nullable = false)
    private LocalDate dateExpiration;

    @NotNull(message = "A porcentagem de ganho é obrigatória.")
    @DecimalMin(value = "0.00", inclusive = true, message = "A porcentagem de ganho não pode ser negativa.")
    @Digits(integer = 3, fraction = 2, message = "A porcentagem de ganho deve ter no máximo 3 dígitos inteiros e 2 decimais.")
    @Column(nullable = false)
    private BigDecimal gainPercentage;

    @NotNull(message = "O preço por lote com percentual é obrigatório.")
    @DecimalMin(value = "0.00", message = "O preço por lote com percentual deve ser maior ou igual a zero.")
    @Digits(integer = 10, fraction = 2, message = "O preço por lote com percentual deve ter no máximo 10 dígitos inteiros e 2 decimais.")
    @Column(nullable = false)
    private BigDecimal priceForLotePercent;

    @NotNull(message = "O preço por lote é obrigatório.")
    @DecimalMin(value = "0.00", message = "O preço por lote deve ser maior ou igual a zero.")
    @Digits(integer = 10, fraction = 2, message = "O preço por lote deve ter no máximo 10 dígitos inteiros e 2 decimais.")
    @Column(nullable = false)
    private BigDecimal priceForLote;

    @DecimalMin(value = "0.00", message = "O preço unitário com percentual deve ser maior ou igual a zero.")
    @Digits(integer = 10, fraction = 2, message = "O preço unitário com percentual deve ter no máximo 10 dígitos inteiros e 2 decimais.")
    private BigDecimal priceForUnityPercent;

    @DecimalMin(value = "0.00", message = "O preço unitário deve ser maior ou igual a zero.")
    @Digits(integer = 10, fraction = 2, message = "O preço unitário deve ter no máximo 10 dígitos inteiros e 2 decimais.")
    private BigDecimal priceForUnity;

    @ManyToOne
    @JoinColumn(name = "promotion_id", referencedColumnName = "promotionId", nullable = true)
    private Promotion promotion;

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String status = "";

    public Product() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
