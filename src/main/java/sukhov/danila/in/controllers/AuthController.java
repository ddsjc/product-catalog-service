package sukhov.danila.in.controllers;

import sukhov.danila.domain.services.AuthService;
import sukhov.danila.domain.entities.UserEntity;

import java.util.Scanner;

public class AuthController {
    private final AuthService authService;
    private final Scanner scanner;

    public AuthController(AuthService authService, Scanner scanner) {
        this.authService = authService;
        this.scanner = scanner;
    }

    public UserEntity showAuthMenu() {
        while (true) {
            System.out.println("\n1. Вход");
            System.out.println("2. Регистрация");
            System.out.println("3. Выход");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    UserEntity user = authService.login();
                    if (user != null) return user;
                }
                case "2" -> authService.register();
                case "3" -> {
                    return null;
                }
                default -> System.out.println("Некорректный выбор.");
            }
        }
    }
}
