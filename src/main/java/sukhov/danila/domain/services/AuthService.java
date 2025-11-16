package sukhov.danila.domain.services;

import sukhov.danila.domain.exceptions.AlreadyExistsException;
import sukhov.danila.domain.repositories.UserRepository;
import sukhov.danila.domain.services.helpers.PasswordUtil;
import sukhov.danila.domain.entities.ERole;
import sukhov.danila.domain.entities.UserEntity;
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
    private final AuditService auditService;
    private final Scanner scanner;

    public AuthService(UserRepository userRepository, AuditService auditService, Scanner scanner) {
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.scanner = scanner;
    }

    public UserEntity register() {
        System.out.print("Имя пользователя: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            System.out.println("Имя не может быть пустым.");
            return null;
        }
        if (userRepository.findByName(username).isPresent()) {
            throw new AlreadyExistsException("Пользователь '" + username + "' уже существует");
        }

        System.out.print("Пароль: ");
        String password = scanner.nextLine();
        if (password.isEmpty()) {
            System.out.println("Пароль не может быть пустым.");
            return null;
        }

        System.out.println("Роль: 1 - Покупатель, 2 - Продавец");
        String roleChoice = scanner.nextLine().trim();
        String role = "2".equals(roleChoice) ? ERole.SELLER.name() : ERole.USER.name();

        String passwordHash = PasswordUtil.hashPassword(password);
        UserEntity user = UserEntity.builder()
                .username(username)
                .passwordHash(passwordHash)
                .role(role)
                .build();

        user = userRepository.save(user);
        auditService.log(username, "регистрация (роль: " + role + ")");
        System.out.println("Регистрация успешна!");
        return user;
    }

    public UserEntity login() {
        System.out.print("Имя пользователя: ");
        String username = scanner.nextLine().trim();
        UserEntity user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        System.out.print("Пароль: ");
        String password = scanner.nextLine();
        if (!PasswordUtil.successSignIn(password, user.getPasswordHash())) {
            System.out.println("Неверный пароль.");
            return null;
        }

        auditService.log(username, "вход в систему");
        System.out.println("Добро пожаловать, " + username + "!");
        return user;
    }
}
