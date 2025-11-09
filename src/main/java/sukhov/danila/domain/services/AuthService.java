package sukhov.danila.domain.services;

import sukhov.danila.domain.helpers.PasswordUtil;
import sukhov.danila.domain.entities.ERole;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.out.repositories.UserRepository;

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
 * Использует {@link UserRepository} для взаимодействия с хранилищем пользователей,
 * {@link PasswordUtil} для работы с паролями, и {@link AuditService} для записи логов.
 *
 * @author
 *     Данила Сухов
 * @version 1.0
 */
public class AuthService {
    private PasswordUtil passwordHasher;
    private UserRepository userRepository;
    private AuditService auditService;
    private Scanner scanner;

    public AuthService(UserRepository userRepository, AuditService auditService, Scanner scanner) {
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.scanner = scanner;
    }

    /**
     * Регистрирует нового пользователя в системе.
     * <p>Проверяет наличие пользователя, запрашивает данные и сохраняет их в хранилище.</p>
     *
     * @return созданный {@link UserEntity} или {@code null}, если регистрация не удалась
     */
    public UserEntity register() {
        System.out.print("Введите username: ");
        String username = scanner.nextLine();
        if (userRepository.getUser(username) != null) { //вынести в контроллер хелпер!!!
            System.out.println("Пользователь уже существует!");
            return null;
        }

        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        System.out.println("Выберите роль: 1 - Покупатель, 2 - Продавец");
        String choice = scanner.nextLine();
        ERole role = choice.equals("2") ? ERole.SELLER : ERole.USER;

        String hash = passwordHasher.hashPassword(password);
        UserEntity newUser = new UserEntity(username, hash, role);
        if (userRepository.addUser(newUser)) {
            auditService.log(newUser.getUsername(), String.format("Регистрация нового пользователя: " + username + " роль: " + role));
            System.out.println("Регистрация успешна!");
            return newUser;
        }
        return null;
    }

    /**
     * Выполняет вход пользователя в систему.
     * <p>Проверяет корректность логина и пароля, записывает факт входа в аудит.</p>
     *
     * @return объект {@link UserEntity} при успешной аутентификации, иначе {@code null}
     */
    public UserEntity login() {
        System.out.print("Введите username: ");
        String username = scanner.nextLine();
        UserEntity user = userRepository.getUser(username);
        if (user == null) {
            System.out.println("Пользователь не найден");
            return null;
        }
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        if (passwordHasher.successSignIn(password, user.getPasswordHash())) {
            auditService.log(username,"Вход пользователя в систему");
            System.out.println("Вход успешен!");
            return user;
        } else {
            System.out.println("Неверный пароль");
            return null;
        }
    }
}
