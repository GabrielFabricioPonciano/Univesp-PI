package com.univesp.projeto_integrador.service;

import com.univesp.projeto_integrador.dto.ProductDTO;
import com.univesp.projeto_integrador.dto.PromotionDTO;
import com.univesp.projeto_integrador.exception.ResourceNotFoundException;
import com.univesp.projeto_integrador.model.Product;
import com.univesp.projeto_integrador.model.Promotion;
import com.univesp.projeto_integrador.repository.ProductRepository;
import com.univesp.projeto_integrador.yuxi.ProductCalculator;
import com.univesp.projeto_integrador.yuxi.ProductMapper;
import com.univesp.projeto_integrador.yuxi.ProductValidator;
import com.univesp.projeto_integrador.yuxi.PromotionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
//Refazer depois

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private PromotionMapper promotionMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductValidator productValidator;

    @Autowired
    private ProductCalculator productCalculator;

    // Método para listar todos os produtos
    public List<ProductDTO> findAll() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(productMapper::entityToDto).collect(Collectors.toList());
    }

    // Método para buscar produto por ID
    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id " + id));
        return productMapper.entityToDto(product);
    }

    // Método para deletar produto por ID
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado com o ID: " + id);
        }
        productRepository.deleteById(id);
    }

    // Método para atualizar um produto existente
    public ProductDTO updateProduct(Long id, ProductDTO newProductDetails) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id " + id));

        // Valida os campos numéricos
        productValidator.validateBigDecimalField(newProductDetails.getPriceForLote(), "O preço por lote");
        productValidator.validateBigDecimalField(newProductDetails.getGainPercentage(), "A porcentagem de ganho");

        // Atualiza os campos do produto manualmente
        existingProduct.setProductName(newProductDetails.getProductName());
        existingProduct.setProductType(newProductDetails.getProductType());
        existingProduct.setQuantity(newProductDetails.getQuantity());
        existingProduct.setNumberLote(newProductDetails.getNumberLote());
        existingProduct.setDescription(newProductDetails.getDescription());
        existingProduct.setDateExpiration(newProductDetails.getDateExpiration());
        existingProduct.setGainPercentage(newProductDetails.getGainPercentage());
        existingProduct.setPriceForLote(newProductDetails.getPriceForLote());
        existingProduct.setStatus(newProductDetails.getStatus());
        existingProduct.setUpdatedAt(LocalDateTime.now());

        // Gerencia a promoção (adiciona ou remove)
        if (newProductDetails.getPromotion() == null) {
            existingProduct.setPromotion(null);
        } else {
            PromotionDTO promotionDTO = promotionService.getPromotionById(newProductDetails.getPromotion().getPromotionId());
            Promotion promotion = promotionMapper.dtoToEntity(promotionDTO);
            existingProduct.setPromotion(promotion);
        }

        // Calcula os preços com base no lote e na promoção (se houver)
        productCalculator.calculatePrices(existingProduct);

        // Salva o produto atualizado
        Product savedProduct = productRepository.save(existingProduct);
        return productMapper.entityToDto(savedProduct);
    }




    // Método para salvar um novo produto
    public ProductDTO saveProduct(ProductDTO productDTO) {
        Product product = productMapper.dtoToEntity(productDTO);

        // Validações de campos obrigatórios e numéricos
        productValidator.validateBigDecimalField(product.getPriceForLote(), "O preço por lote");
        productValidator.validateBigDecimalField(product.getGainPercentage(), "A porcentagem de ganho");

        // Verifica e associa a promoção, se houver
        if (productDTO.getPromotion() != null) {
            Promotion promotion = promotionMapper.dtoToEntity(
                    promotionService.getPromotionById(productDTO.getPromotion().getPromotionId())
            );
            product.setPromotion(promotion);
        }

        // Cálculo de preços baseado no preço do lote e porcentagem de ganho
        productCalculator.calculatePrices(product);

        // Atualiza os campos de data
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setStatus("ACTIVE");
        // Salva o produto no banco de dados
        Product savedProduct = productRepository.save(product);

        // Retorna o produto salvo como DTO
        return productMapper.entityToDto(savedProduct);
    }

    public List<Product> getProductsByStockLevel(int threshold) {
        return productRepository.findByQuantityLessThan(threshold);
    }
}
