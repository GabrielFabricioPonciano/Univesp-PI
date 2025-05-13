// ProductMapper.java
package com.univesp.projeto_integrador.yuxi;

import com.univesp.projeto_integrador.dto.ProductRequest;
import com.univesp.projeto_integrador.dto.ProductResponse;
import com.univesp.projeto_integrador.model.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "status", source = "status")
    @Mapping(source = "promotion.promotionId", target = "promotionId")
    ProductResponse toResponse(Product product);

    @Mapping(target = "status", qualifiedByName = "stringToProductStatus")
    @Mapping(target = "promotion", ignore = true)
    Product toEntity(ProductRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "status", qualifiedByName = "stringToProductStatus")
    void updateFromRequest(ProductRequest request, @MappingTarget Product product);

    @Named("stringToProductStatus")
    default Product.ProductStatus mapStringToStatus(String status) {
        return Product.ProductStatus.valueOf(status);
    }

    @Named("productStatusToString")
    default String mapStatusToString(Product.ProductStatus status) {
        return status.name();
    }
}