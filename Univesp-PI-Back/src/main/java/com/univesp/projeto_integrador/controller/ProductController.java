package com.univesp.projeto_integrador.controller;

import com.univesp.projeto_integrador.dto.ProductDTO;
import com.univesp.projeto_integrador.exception.ResourceNotFoundException;
import com.univesp.projeto_integrador.model.Product;
import com.univesp.projeto_integrador.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//Refazer depois

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Buscar todos os produtos
    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.findAll();
    }

    @GetMapping("/critical-stock")
    public List<Product> getCriticalStockProducts(@RequestParam int threshold) {
        return productService.getProductsByStockLevel(threshold);
    }

    // Buscar produto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    // Criar novo produto
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO product) {
        ProductDTO createdProduct = productService.saveProduct(product);
        return ResponseEntity.ok(createdProduct);
    }

    // Atualizar produto existente
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDTO productDetails) {
        try {
            // Verifica se os detalhes do produto estão presentes
            if (productDetails == null) {
                return ResponseEntity.badRequest().body("Os detalhes do produto não podem ser nulos.");
            }

            // Realiza a atualização do produto
            ProductDTO updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct);

        } catch (ResourceNotFoundException e) {
            // Retorna 404 se o produto não for encontrado
            return ResponseEntity.status(404).body(e.getMessage());

        } catch (Exception e) {
            // Captura outros erros e retorna 500 (Internal Server Error)
            return ResponseEntity.status(500).body("Erro ao atualizar o produto: " + e.getMessage());
        }
    }

    // Deletar produto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}
