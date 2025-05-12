// PromotionMapper.java
package com.univesp.projeto_integrador.yuxi;

import com.univesp.projeto_integrador.dto.PromotionRequest;
import com.univesp.projeto_integrador.dto.PromotionResponse;
import com.univesp.projeto_integrador.model.Promotion;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    @Mapping(target = "products", ignore = true)
    Promotion toEntity(PromotionRequest request);

    @Mapping(source = "promotionId", target = "id")
    PromotionResponse toResponse(Promotion promotion); // Não inclui produtos

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(PromotionRequest request, @MappingTarget Promotion entity);
}
