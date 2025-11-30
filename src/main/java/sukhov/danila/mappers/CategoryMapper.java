package sukhov.danila.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import sukhov.danila.domain.entities.CategoryEntity;
import sukhov.danila.dtos.CategoryDTO;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(CategoryMapper.class);
    CategoryDTO toDto(CategoryEntity entity);
    CategoryEntity toEntity(CategoryDTO dto);
}
