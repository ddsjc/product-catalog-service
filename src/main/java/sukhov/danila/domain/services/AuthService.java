package sukhov.danila.domain.services;

import sukhov.danila.aspect.AuditAction;
import sukhov.danila.domain.exceptions.AlreadyExistsException;
import sukhov.danila.domain.repositories.UserRepository;
import sukhov.danila.domain.services.helpers.PasswordUtil;
import sukhov.danila.domain.entities.ERole;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.dtos.UserDTO;
import sukhov.danila.mappers.UserMapper;
import sukhov.danila.out.persistence.jdbc.UserRepositoryImpl;

import java.util.Scanner;

/**
 * Сервис аутентификации и регистрации пользователей.
 *
 * <p>Класс отвечает за:
 * <ul>
 *     <li>Регистрацию новых пользователей (с выбором роли)</li>
 *     <li>Авторизацию существующих пользователей</li>
 *     <li>Хеширование паролей и аудит действий</li>
 * </ul>
 *
 * Использует {@link UserRepositoryImpl} для взаимодействия с хранилищем пользователей,
 * {@link PasswordUtil} для работы с паролями, и {@link AuditService} для записи логов.
 *
 * @author
 *     Данила Сухов
 * @version 1.0
 */
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @AuditAction("Зарегистрировался в сервисе")
    public UserDTO register(String username, String password, String role) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Имя пользователя обязательно");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Пароль обязателен");
        }
        if (userRepository.findByName(username).isPresent()) {
            throw new AlreadyExistsException("Пользователь '" + username + "' уже существует");
        }

        String passwordHash = PasswordUtil.hashPassword(password);
        UserEntity user = UserEntity.builder()
                .username(username)
                .passwordHash(passwordHash)
                .role(role)
                .build();

        UserEntity saved = userRepository.save(user);
        return UserMapper.INSTANCE.toDto(saved);
    }
    @AuditAction("Залогинился")
    public UserDTO login(String username, String password) {
        UserEntity user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!PasswordUtil.successSignIn(password, user.getPasswordHash())) {
            throw new RuntimeException("Неверный пароль");
        }

        return UserMapper.INSTANCE.toDto(user);
    }
}
