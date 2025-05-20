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

        // 1. Atualiza os dados da promoção primeiro
        mapper.updateFromRequest(request, existing);

        // 2. Salva a promoção atualizada para garantir que os novos valores estejam disponíveis
        Promotion updatedPromotion = repository.save(existing);

        // 3. Busca todos os produtos vinculados à promoção
        List<Product> products = productRepository.findByPromotion(updatedPromotion);

        // 4. Recalcula os preços com os novos valores da promoção
        products.forEach(product -> {
            priceCalculator.calculatePrices(product);

            // Atualiza explicitamente a promoção no produto (caso de alteração de status/datas)
            product.setPromotion(updatedPromotion);
        });

        // 5. Salva os produtos com os novos preços
        productRepository.saveAll(products);

        return mapper.toResponse(updatedPromotion);
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