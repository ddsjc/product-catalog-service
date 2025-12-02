package sukhov.danila.domain.repositories;

import org.springframework.stereotype.Repository;
import sukhov.danila.domain.entities.UserEntity;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository {
    UserEntity save (UserEntity user);
    Optional<UserEntity> findById (Long userId);
    Optional<UserEntity> findByName (String userName);
    List<UserEntity> findAll();
    void deleteUserById(Long id);
}
