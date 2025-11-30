package sukhov.danila.config;

import sukhov.danila.domain.entities.UserEntity;

public class UserContextProvider implements CurrentUserProvider{
    @Override
    public UserEntity getCurrentUser() {
        return UserContext.getCurrentUser();
    }
}
