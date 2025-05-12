package com.univesp.projeto_integrador.service;

import com.univesp.projeto_integrador.dto.*;
import com.univesp.projeto_integrador.exception.*;
import com.univesp.projeto_integrador.model.*;
import com.univesp.projeto_integrador.repository.*;
import com.univesp.projeto_integrador.yuxi.PriceCalculator;
import com.univesp.projeto_integrador.yuxi.ProductMapper;
import com.univesp.projeto_integrador.yuxi.ProductValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private final PromotionService promotionService;
    private final ProductMapper productMapper;
    private final PriceCalculator priceCalculator;
    private final ProductValidator productValidator;

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        productValidator.validateRequest(request);

        Product product = productMapper.toEntity(request);
        associatePromotion(request, product);
        priceCalculator.calculatePrices(product);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        productValidator.validateRequest(request);
        productMapper.updateFromRequest(request, product);

        // Associe a promoção pelo ID sem carregar a lista de produtos
        if (request.promotionId() != null) {
            Promotion promotion = promotionRepository.findById(request.promotionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Promotion", request.promotionId()));
            product.setPromotion(promotion);
        } else {
            product.setPromotion(null);
        }

        priceCalculator.calculatePrices(product);
        Product savedProduct = productRepository.save(product); // 👈 Salvamento explícito

        return productMapper.toResponse(savedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", id);
        }
        productRepository.deleteById(id);
    }

    private void associatePromotion(ProductRequest request, Product product) {
        if (request.promotionId() != null && request.promotionId() > 0) {
            Promotion promotion = promotionService.getPromotionEntityById(request.promotionId());
            product.setPromotion(promotion);
        } else {
            product.setPromotion(null);
        }
    }

    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id)); // Formato correto
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts(int threshold) {
        return productRepository.findByQuantityLessThan(threshold).stream()
                .map(productMapper::toResponse)
                .toList();
    }
}