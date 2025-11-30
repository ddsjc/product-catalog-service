package sukhov.danila.domain.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sukhov.danila.domain.entities.ProductEntity;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.domain.exceptions.NotFoundException;
import sukhov.danila.domain.repositories.UserRepository;
import sukhov.danila.dtos.UserDTO;
import sukhov.danila.mappers.ProductMapper;
import sukhov.danila.mappers.UserMapper;

import java.util.Optional;

@Transactional
@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public UserDTO findUserById(Long id){
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Товар с ID " + id + " не найден"));
        return UserMapper.INSTANCE.toDto(user);
    }
}
