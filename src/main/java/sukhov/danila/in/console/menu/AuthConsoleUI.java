package sukhov.danila.in.console.menu;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.domain.services.AuthService;

import java.util.Scanner;
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthConsoleUI {
    AuthService authService;
    Scanner scanner;

    public AuthConsoleUI(AuthService authService, Scanner scanner) {
        this.authService = authService;
        this.scanner = scanner;
    }

    public UserEntity showAuthMenu() {
        while (true) {
            System.out.println("\n1. Вход");
            System.out.println("2. Регистрация");
            System.out.println("3. Выход");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> {
                        UserEntity user = authService.login();
                        if (user != null) return user;
                    }
                    case "2" -> {
                        authService.register(); // если register() возвращает void — ок
                        // или: return authService.register(); если должен возвращать
                    }
                    case "3" -> {
                        return null;
                    }
                    default -> System.out.println("Некорректный выбор.");
                }
            } catch (Exception e) {
                System.err.println("Критическая ошибка: " + e.getMessage());
                e.printStackTrace(); // ← добавь это!
            }
        }
    }
}
