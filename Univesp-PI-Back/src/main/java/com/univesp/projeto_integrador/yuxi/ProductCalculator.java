package com.univesp.projeto_integrador.yuxi;

import com.univesp.projeto_integrador.model.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.apache.el.lang.ELArithmetic.multiply;
//Refazer depois

@Component
public class ProductCalculator {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    // Método principal para calcular todos os preços relacionados ao produto
    public void calculatePrices(Product product) {
        // Calcula o preço unitário base
        BigDecimal priceForUnity = calculateUnitPrice(product.getPriceForLote(), product.getQuantity());
        product.setPriceForUnity(priceForUnity);

        // Calcula o preço do lote com margem de ganho
        BigDecimal priceForLotePercent = calculatePriceForLotePercent(product.getPriceForLote(), product.getGainPercentage());
        product.setPriceForLotePercent(priceForLotePercent);

        // Calcula o preço unitário com margem de ganho
        BigDecimal priceForUnityPercent = calculateUnitPrice(priceForLotePercent, product.getQuantity());
        product.setPriceForUnityPercent(priceForUnityPercent);

        // Aplica o desconto da promoção, se houver
        if (product.getPromotion() != null && product.getPromotion().getDiscountPercentage() != null) {
            BigDecimal discountFactor = calculateDiscountFactor(product.getPromotion().getDiscountPercentage());
            priceForLotePercent = applyDiscount(priceForLotePercent, discountFactor);
            priceForUnityPercent = calculateUnitPrice(priceForLotePercent, product.getQuantity()); // Recalcula o preço unitário com desconto

            product.setPriceForLotePercent(priceForLotePercent);
            product.setPriceForUnityPercent(priceForUnityPercent);
        }
    }

    private BigDecimal calculatePriceForLotePercent(BigDecimal priceForLote, BigDecimal gainPercentage) {
        BigDecimal hundred = BigDecimal.valueOf(100);
        // Converte a porcentagem para um fator multiplicador (ex: 50% -> 1.50)
        BigDecimal gainFactor = gainPercentage.add(hundred).divide(hundred, 2, RoundingMode.HALF_UP);
        // Calcula o preço com o ganho aplicado
        return priceForLote.multiply(gainFactor).setScale(2, RoundingMode.HALF_UP);
    }


    // Cálculo do preço unitário
    private BigDecimal calculateUnitPrice(BigDecimal priceForLote, int quantity) {
        if (isValidPriceAndQuantity(priceForLote, quantity)) {
            return priceForLote.divide(BigDecimal.valueOf(quantity), 2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    // Cálculo do fator de desconto
    private BigDecimal calculateDiscountFactor(BigDecimal discountPercentage) {
        return BigDecimal.ONE.subtract(discountPercentage.divide(ONE_HUNDRED, RoundingMode.HALF_UP));
    }

    // Aplicação do desconto
    private BigDecimal applyDiscount(BigDecimal price, BigDecimal discountFactor) {
        return price.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
    }

    // Validações
    private boolean isValidPriceAndPercentage(BigDecimal price, BigDecimal percentage) {
        return price != null && percentage != null && price.compareTo(BigDecimal.ZERO) > 0 && percentage.compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean isValidPriceAndQuantity(BigDecimal price, int quantity) {
        return price != null && quantity > 0 && price.compareTo(BigDecimal.ZERO) > 0;
    }
}
