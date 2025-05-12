package com.univesp.projeto_integrador.yuxi;

import com.univesp.projeto_integrador.exception.InvalidDataException;
import com.univesp.projeto_integrador.model.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PriceCalculator {

    /**
     * Calcula unitPrice (preço unitário sem margem) e finalPrice (com margem e eventual desconto).
     */
    public void calculatePrices(Product product) {
        calculateBasePrices(product);
        applyPromotionDiscount(product);
    }

    private void calculateBasePrices(Product product) {
        int quantity = product.getQuantity();
        if (quantity <= 0) {
            throw new InvalidDataException("Quantity must be greater than zero");
        }

        BigDecimal batchPrice = product.getBatchPrice();
        if (batchPrice == null) {
            throw new InvalidDataException("Batch price must not be null");
        }

        // unitPrice: custo unitário sem margem de lucro
        BigDecimal unitCost = batchPrice.divide(
                BigDecimal.valueOf(quantity), 4, RoundingMode.HALF_UP
        );
        product.setUnitPrice(unitCost.setScale(2, RoundingMode.HALF_UP));

        // finalPrice inicial: unitCost com margem de lucro
        BigDecimal marginFactor = product.getProfitMargin()
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal priceWithMargin = unitCost.multiply(BigDecimal.ONE.add(marginFactor));
        product.setFinalPrice(priceWithMargin.setScale(2, RoundingMode.HALF_UP));
    }

    private void applyPromotionDiscount(Product product) {
        if (product.hasActivePromotion()) {
            BigDecimal discountFactor = product.getPromotion().getDiscountPercentage()
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

            BigDecimal precoAntesDesconto = product.getFinalPrice();
            BigDecimal discounted = precoAntesDesconto.multiply(BigDecimal.ONE.subtract(discountFactor))
                    .setScale(2, RoundingMode.HALF_UP);

            product.setFinalPrice(discounted);
            System.out.println("Desconto aplicado: " + precoAntesDesconto + " -> " + discounted); // 👈 Log
        } else {
            System.out.println("Promoção não está ativa para o produto " + product.getProductId()); // 👈 Log
        }
    }
}