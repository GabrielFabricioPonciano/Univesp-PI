package com.univesp.projeto_integrador.yuxi;

import com.univesp.projeto_integrador.dto.ProductDTO;
import com.univesp.projeto_integrador.model.Product;
import com.univesp.projeto_integrador.model.Promotion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ProductMapper {

    @Autowired
    private PromotionMapper promotionMapper;

    public Product dtoToEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setProductName(productDTO.getProductName());
        product.setProductType(productDTO.getProductType());
        product.setQuantity(productDTO.getQuantity());
        product.setNumberLote(productDTO.getNumberLote());
        product.setDateExpiration(productDTO.getDateExpiration());
        product.setGainPercentage(productDTO.getGainPercentage());
        product.setPriceForLote(productDTO.getPriceForLote());
        product.setDescription(productDTO.getDescription());
        product.setStatus(productDTO.getStatus());

        // Adiciona promoção associada, se houver
        if (productDTO.getPromotion() != null) {
            Promotion promotion = promotionMapper.dtoToEntity(productDTO.getPromotion());
            product.setPromotion(promotion);
        }

        return product;
    }

    public ProductDTO entityToDto(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setProductType(product.getProductType());
        dto.setQuantity(product.getQuantity());
        dto.setNumberLote(product.getNumberLote());
        dto.setDateExpiration(product.getDateExpiration());
        dto.setGainPercentage(product.getGainPercentage());
        dto.setPriceForLote(product.getPriceForLote());
        dto.setDescription(product.getDescription());
        dto.setPriceForUnity(product.getPriceForUnity());
        dto.setPriceForUnityPercent(product.getPriceForUnityPercent());
        dto.setPriceForLotePercent(product.getPriceForLotePercent());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        dto.setStatus(product.getStatus());

        // Adiciona promoção associada, se houver
        if (product.getPromotion() != null) {
            dto.setPromotion(promotionMapper.entityToDto(product.getPromotion()));
        }

        return dto;
    }
}

