package com.univesp.projeto_integrador.repository;

import com.univesp.projeto_integrador.model.Product;
import com.univesp.projeto_integrador.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Encontra produtos com estoque abaixo do nível especificado
     * @param threshold Nível mínimo de estoque
     * @return Lista de produtos com estoque crítico
     */
    @Query("SELECT p FROM Product p WHERE p.quantity < :threshold AND p.status = 'ACTIVE'")
    List<Product> findByQuantityLessThan(int threshold);

    List<Product> findByPromotion(Promotion promotion);
}