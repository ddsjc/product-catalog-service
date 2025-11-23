package sukhov.danila.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.dtos.UserDTO;
//(builder = @Builder)
@Mapper
public interface UserMapper {
    UserMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(UserMapper.class);
    UserDTO toDto(UserEntity entity);
    UserEntity toEntity(UserDTO dto);
}
