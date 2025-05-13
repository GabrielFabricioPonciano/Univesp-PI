package com.univesp.projeto_integrador.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private String batchNumber;

    @Lob
    private String description;

    @NotNull(message = "A data de validade é obrigatória.")
    @Future(message = "A data de validade deve ser uma data futura.")
    @Column(nullable = false)
    private LocalDate expirationDate;

    @NotNull(message = "A porcentagem de ganho é obrigatória.")
    @DecimalMin(value = "0.00", message = "A porcentagem de ganho não pode ser negativa.")
    @Digits(integer = 3, fraction = 2)
    @Column(nullable = false)
    private BigDecimal profitMargin;

    // Campos calculados
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false)
    private BigDecimal batchPrice;

    @Digits(integer = 10, fraction = 2)
    private BigDecimal unitPrice;

    // Relacionamento com promoções
    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Digits(integer = 10, fraction = 2)
    private BigDecimal finalPrice;

    // Auditoria
    @Column(updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default // 👈 Garante que o valor padrão seja usado mesmo com Lombok @Builder
    private ProductStatus status = ProductStatus.ACTIVE;



    public enum ProductStatus {
        ACTIVE, INACTIVE, DISCONTINUED
    }

    public boolean hasActivePromotion() {
        if (promotion == null) return false;
        return promotion.getStatus() == Promotion.Status.ACTIVE &&
                !LocalDate.now().isBefore(promotion.getStartDate()) &&
                !LocalDate.now().isAfter(promotion.getEndDate());
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}