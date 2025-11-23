package sukhov.danila.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import sukhov.danila.domain.entities.ProductEntity;
import sukhov.danila.dtos.ProductDTO;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(ProductMapper.class);
    ProductDTO toDto(ProductEntity entity);
    ProductEntity toEntity(ProductDTO dto);
}
