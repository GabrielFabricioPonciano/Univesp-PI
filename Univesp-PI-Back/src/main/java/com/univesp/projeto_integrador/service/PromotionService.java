package com.univesp.projeto_integrador.service;

import com.univesp.projeto_integrador.dto.*;
import com.univesp.projeto_integrador.exception.*;
import com.univesp.projeto_integrador.model.*;
import com.univesp.projeto_integrador.repository.*;
import com.univesp.projeto_integrador.yuxi.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository repository;
    private final ProductRepository productRepository;
    private final PromotionMapper mapper;
    private final PriceCalculator priceCalculator;

    public PromotionResponse create(PromotionRequest request) {
        Promotion promotion = mapper.toEntity(request);
        return mapper.toResponse(repository.save(promotion));
    }

    public List<PromotionResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public PromotionResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Promoção", id));
    }

    public PromotionResponse update(Long id, PromotionRequest request) {
        Promotion existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoção", id));

        mapper.updateFromRequest(request, existing);
        return mapper.toResponse(repository.save(existing));
    }

    public void delete(Long id) {
        Promotion promotion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoção", id));

        List<Product> products = productRepository.findByPromotion(promotion);

        products.forEach(product -> {
            product.setPromotion(null); // 👈 Remove a promoção
            priceCalculator.calculatePrices(product); // 👈 Recalcula o preço SEM desconto
        });

        productRepository.saveAll(products); // 👈 Salva os produtos atualizados
        repository.delete(promotion);
    }

    public Promotion getPromotionEntityById(Long promotionId) {
        return repository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", promotionId));
    }
}