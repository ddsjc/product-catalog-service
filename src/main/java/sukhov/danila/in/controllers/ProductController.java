package sukhov.danila.in.controllers;

import sukhov.danila.domain.services.ProductService;
import sukhov.danila.domain.entities.ERole;
import sukhov.danila.domain.entities.UserEntity;

import java.util.Scanner;

public class ProductController {
    private final ProductService productService;
    private final Scanner scanner;

    public ProductController(ProductService productService, Scanner scanner) {
        this.productService = productService;
        this.scanner = scanner;
    }

    public void handleMenu(UserEntity currentUser) {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Меню товаров ---");
            System.out.println("1. Список товаров");
            System.out.println("2. Поиск товаров");

            if (currentUser.getRole() != ERole.USER) {
                System.out.println("3. Добавить товар");
                System.out.println("4. Редактировать товар");
                System.out.println("5. Удалить товар");
            }

            System.out.println("0. Назад");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1" -> productService.listProducts();
                    case "2" -> productService.searchProducts();
                    case "3" -> {
                        if (currentUser.getRole() == ERole.USER)
                            System.out.println("Нет доступа!");
                        else
                            productService.addProduct(currentUser);
                    }
                    case "4" -> {
                        if (currentUser.getRole() == ERole.USER)
                            System.out.println("Нет доступа!");
                        else
                            productService.editProduct(currentUser);
                    }
                    case "5" -> {
                        if (currentUser.getRole() == ERole.USER)
                            System.out.println("Нет доступа!");
                        else
                            productService.removeProduct(currentUser);
                    }
                    case "0" -> running = false;
                    default -> System.out.println("Некорректный выбор.");
                }
            } catch (RuntimeException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }
}
