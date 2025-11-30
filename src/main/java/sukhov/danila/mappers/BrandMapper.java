package sukhov.danila.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import sukhov.danila.domain.entities.BrandEntity;
import sukhov.danila.dtos.BrandDTO;

@Mapper
public interface BrandMapper {
    BrandMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(BrandMapper.class);
    BrandDTO toDto(BrandEntity entity);
    BrandEntity toEntity(BrandDTO dto);
}
