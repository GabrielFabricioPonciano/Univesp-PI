package com.univesp.projeto_integrador.service;

import com.univesp.projeto_integrador.dto.PromotionDTO;
import com.univesp.projeto_integrador.exception.ResourceNotFoundException;
import com.univesp.projeto_integrador.model.Promotion;
import com.univesp.projeto_integrador.repository.PromotionRepository;
import com.univesp.projeto_integrador.yuxi.PromotionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
//Refazer depois

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionMapper promotionMapper;

    // Criar promoção
    public PromotionDTO createPromotion(PromotionDTO promotionDTO) {
        Promotion promotion = promotionMapper.dtoToEntity(promotionDTO);
        promotion = promotionRepository.save(promotion);
        return promotionMapper.entityToDto(promotion);
    }

    // Listar todas as promoções
    public List<PromotionDTO> getAllPromotions() {
        List<Promotion> promotions = promotionRepository.findAll();
        return promotions.stream().map(promotionMapper::entityToDto).toList();
    }

    // Buscar promoção por ID
    public PromotionDTO getPromotionById(Long promotionId) {
        return promotionRepository.findById(promotionId)
                .map(promotionMapper::entityToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Promoção não encontrada com id " + promotionId));
    }


    // Atualizar promoção
    public PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO) {
        Promotion existingPromotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoção não encontrada com id " + id));

        promotionMapper.updatePromotionFromDto(existingPromotion, promotionDTO);
        promotionRepository.save(existingPromotion);

        return promotionMapper.entityToDto(existingPromotion);
    }

    // Deletar promoção
    public void deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Promoção não encontrada com id " + id);
        }
        promotionRepository.deleteById(id);
    }
}
