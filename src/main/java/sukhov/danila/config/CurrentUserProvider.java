package sukhov.danila.config;

import sukhov.danila.domain.entities.UserEntity;

public interface CurrentUserProvider {
    UserEntity getCurrentUser();
}
