package sukhov.danila.in.console.menu;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.domain.services.BrandService;
import sukhov.danila.in.console.command.Command;

import java.util.Map;
import java.util.Scanner;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandConsoleUI {
    BrandService brandService;
    UserEntity currentUser;
    Scanner scanner;
    Map<String, Command> commands;

    public BrandConsoleUI(BrandService brandService, UserEntity currentUser, Scanner scanner) {
        this.brandService = brandService;
        this.currentUser = currentUser;
        this.scanner = scanner;
        this.commands = Map.of(
                "1", () -> brandService.createBrand(currentUser),
                "2", brandService::listBrands
        );
    }

    public void show() {
        while (true) {
            System.out.println("\n=== Бренды ===");
            System.out.println("1. Добавить бренд");
            System.out.println("2. Просмотреть все бренды");
            System.out.println("0. Назад");

            String choice = scanner.nextLine().trim();
            if ("0".equals(choice)) break;

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
}
