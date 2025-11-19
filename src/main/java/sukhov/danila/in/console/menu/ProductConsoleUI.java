package sukhov.danila.in.console.menu;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import sukhov.danila.domain.entities.ERole;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.domain.services.ProductService;
import sukhov.danila.in.console.command.Command;

import java.util.Map;
import java.util.Scanner;
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductConsoleUI {
    ProductService productService;
    UserEntity currentUser;
    Scanner scanner;
    Map<String, Command> commands;

    public ProductConsoleUI(ProductService productService, UserEntity currentUser, Scanner scanner) {
        this.productService = productService;
        this.currentUser = currentUser;
        this.scanner = scanner;
        this.commands = Map.of(
                "1", productService::listProducts,
                "2", productService::searchProducts,
                "3", () -> productService.addProduct(currentUser),
                "4", () -> productService.editProduct(currentUser),
                "5", () -> productService.removeProduct(currentUser)
        );
    }

    public void show() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            if ("0".equals(choice)) break;

            if (!hasAccess(choice)) {
                System.out.println("Нет доступа!");
                continue;
            }

            Command cmd = commands.get(choice);
            if (cmd != null) {
                try {
                    cmd.execute();
                } catch (Exception e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            } else {
                System.out.println("Неверный выбор.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- Меню товаров ---");
        System.out.println("1. Список товаров");
        System.out.println("2. Поиск товаров");
        if (!ERole.USER.name().equals(currentUser.getRole())) {
            System.out.println("3. Добавить товар");
            System.out.println("4. Редактировать товар");
            System.out.println("5. Удалить товар");
        }
        System.out.println("0. Назад");
    }

    private boolean hasAccess(String choice) {
        return ERole.USER.name().equals(currentUser.getRole())
                ? ("1".equals(choice) || "2".equals(choice))
                : true;
    }
}
