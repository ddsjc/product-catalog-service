package sukhov.danila.out.repositories;

import sukhov.danila.out.storage.FileStorage;
import sukhov.danila.domain.entities.UserEntity;

import java.util.Collection;
import java.util.Map;

public class UserRepository {
    private final String file = "users.ser";
    private final Map<String, UserEntity> users;

    public UserRepository() {
        users = FileStorage.loadMap(file);
    }

    public boolean addUser(UserEntity user) {
        if (users.containsKey(user.getUsername())) return false;
        users.put(user.getUsername(), user);
        FileStorage.saveMap(file, users);
        return true;
    }

    public UserEntity getUser(String username) {
        return users.get(username);
    }

    public Collection<UserEntity> getAllUsers() {
        return users.values();
    }

}
