// ProductMapper.java
package com.univesp.projeto_integrador.yuxi;

import com.univesp.projeto_integrador.dto.ProductRequest;
import com.univesp.projeto_integrador.dto.ProductResponse;
import com.univesp.projeto_integrador.model.Product;
import com.univesp.projeto_integrador.model.Promotion;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "promotion.promotionId", target = "promotionId")
    ProductResponse toResponse(Product product);

    @Mapping(target = "promotion", ignore = true)
    Product toEntity(ProductRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(ProductRequest request, @MappingTarget Product product);
}
