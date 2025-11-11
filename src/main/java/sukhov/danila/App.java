package sukhov.danila;

import sukhov.danila.domain.helpers.PasswordUtil;
import sukhov.danila.domain.services.*;
import sukhov.danila.in.controllers.AuthController;
import sukhov.danila.in.controllers.BrandController;
import sukhov.danila.in.controllers.ProductController;
import sukhov.danila.domain.entities.ERole;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.out.repositories.BrandRepository;
import sukhov.danila.out.repositories.CategoryRepository;
import sukhov.danila.out.repositories.ProductRepository;
import sukhov.danila.out.repositories.UserRepository;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PasswordUtil passwordHasher = new PasswordUtil();

        UserRepository userRepository = new UserRepository();
        BrandRepository brandRepository = new BrandRepository();
        CategoryRepository categoryRepository = new CategoryRepository();
        ProductRepository productRepository = new ProductRepository();
        AuditService auditService = new AuditService();
        CacheService cacheService = new CacheService();

        if (userRepository.getAllUsers().stream().noneMatch(u -> u.getRole() == ERole.ADMIN)) {
            String hash = passwordHasher.hashPassword("admin");
            userRepository.addUser(new UserEntity("admin", hash, ERole.ADMIN));
            System.out.println("Создан админ по умолчанию: username=admin, пароль=admin");
        }

        AuthService authService = new AuthService(userRepository, auditService, scanner);
        BrandService brandService = new BrandService(brandRepository, auditService, scanner);
        ProductService productService = new ProductService(productRepository, brandRepository, categoryRepository, auditService, cacheService, scanner);

        AuthController authController = new AuthController(authService, scanner);
        BrandController brandController = new BrandController(brandService, scanner);
        ProductController productController = new ProductController(productService, scanner);

        UserEntity currentUser = null;

        while (true) {
            if (currentUser == null) {
                currentUser = authController.showAuthMenu();
                if (currentUser == null) break;
                continue;
            }

            System.out.println("\n--- Главное меню ---");
            System.out.println("Текущий пользователь: " + currentUser.getUsername() + " | Роль: " + currentUser.getRole());
            System.out.println("1. Товары");
            System.out.println("2. Бренды");
            System.out.println("0. Выйти из аккаунта");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> productController.handleMenu(currentUser);
                case "2" -> brandController.handleMenu(currentUser);
                case "0" -> {
                    System.out.println("Вы вышли из аккаунта");
                    currentUser = null;
                }
                default -> System.out.println("Некорректный выбор.");
            }
        }

        System.out.println("Программа завершена.");
    }
}

