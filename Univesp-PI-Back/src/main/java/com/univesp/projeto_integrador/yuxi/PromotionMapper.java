package com.univesp.projeto_integrador.yuxi;

import com.univesp.projeto_integrador.dto.PromotionDTO;
import com.univesp.projeto_integrador.model.Promotion;
import org.springframework.stereotype.Component;

@Component
public class PromotionMapper {

    // Converte de DTO para entidade
    public Promotion dtoToEntity(PromotionDTO promotionDTO) {
        Promotion promotion = new Promotion();
        promotion.setPromotionId(promotionDTO.getPromotionId());
        promotion.setPromotionDescription(promotionDTO.getPromotionDescription());
        promotion.setStartDate(promotionDTO.getStartDate());
        promotion.setEndDate(promotionDTO.getEndDate());
        promotion.setDiscountPercentage(promotionDTO.getDiscountPercentage());
        promotion.setStatus(promotionDTO.getStatus());
        return promotion;
    }

    // Converte de entidade para DTO
    public PromotionDTO entityToDto(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        dto.setPromotionId(promotion.getPromotionId());
        dto.setPromotionDescription(promotion.getPromotionDescription());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setDiscountPercentage(promotion.getDiscountPercentage());
        dto.setStatus(promotion.getStatus());
        return dto;
    }

    // Atualiza entidade existente a partir do DTO
    public void updatePromotionFromDto(Promotion promotion, PromotionDTO promotionDTO) {
        promotion.setPromotionDescription(promotionDTO.getPromotionDescription());
        promotion.setStartDate(promotionDTO.getStartDate());
        promotion.setEndDate(promotionDTO.getEndDate());
        promotion.setDiscountPercentage(promotionDTO.getDiscountPercentage());

        // Corrige a aplicação do status, sem usar @NotNull no método
        promotion.setStatus(promotionDTO.getStatus());
    }

}
