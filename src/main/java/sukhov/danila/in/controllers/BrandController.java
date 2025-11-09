package sukhov.danila.in.controllers;

import sukhov.danila.domain.services.BrandService;
import sukhov.danila.domain.entities.ERole;
import sukhov.danila.domain.entities.UserEntity;

import java.util.Scanner;

public class BrandController {
    private final BrandService brandService;
    private final Scanner scanner;

    public BrandController(BrandService brandService, Scanner scanner) {
        this.brandService = brandService;
        this.scanner = scanner;
    }

    public void handleMenu(UserEntity currentUser) {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Меню брендов ---");
            System.out.println("1. Список брендов");
            if (currentUser.getRole() != ERole.USER)
                System.out.println("2. Создать бренд");
            System.out.println("0. Назад");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> brandService.listBrands();
                case "2" -> {
                    if (currentUser.getRole() == ERole.USER)
                        System.out.println("Нет доступа!");
                    else
                        brandService.createBrand(currentUser);
                }
                case "0" -> running = false;
                default -> System.out.println("Некорректный выбор.");
            }
        }
    }
}
