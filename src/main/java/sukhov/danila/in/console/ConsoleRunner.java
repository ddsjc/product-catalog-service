package sukhov.danila.in.console;

import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.domain.repositories.BrandRepository;
import sukhov.danila.domain.repositories.CategoryRepository;
import sukhov.danila.domain.repositories.ProductRepository;
import sukhov.danila.domain.repositories.UserRepository;
import sukhov.danila.domain.services.*;
import sukhov.danila.in.console.menu.AuthConsoleUI;
import sukhov.danila.in.console.menu.BrandConsoleUI;
import sukhov.danila.in.console.menu.ProductConsoleUI;
import sukhov.danila.out.persistence.jdbc.BrandRepositoryImpl;
import sukhov.danila.out.persistence.jdbc.CategoryRepositoryImpl;
import sukhov.danila.out.persistence.jdbc.ProductRepositoryImpl;
import sukhov.danila.out.persistence.jdbc.UserRepositoryImpl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.Scanner;
public class ConsoleRunner {
    public static void main(String[] args) {
        Properties props = new Properties();
        try (InputStream is = ConsoleRunner.class.getResourceAsStream("/application.properties")) {
            props.load(is);
        } catch (IOException e) {
            System.err.println("Не найден application.properties");
            return;
        }

        String url = props.getProperty("db.url");
        String username = props.getProperty("db.username");
        String password = props.getProperty("db.password");

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            Scanner scanner = new Scanner(System.in);

            UserRepository userRepo = new UserRepositoryImpl(conn);
            BrandRepository brandRepo = new BrandRepositoryImpl(conn);
            CategoryRepository categoryRepo = new CategoryRepositoryImpl(conn);
            ProductRepository productRepo = new ProductRepositoryImpl(conn);

            AuditService auditService = new AuditService();
            CacheService cacheService = new CacheService();

            AuthService authService = new AuthService(userRepo, auditService, scanner);
            BrandService brandService = new BrandService(brandRepo, auditService, scanner);
            ProductService productService = new ProductService(
                    productRepo, brandRepo, categoryRepo,
                    auditService, scanner, brandService, cacheService
            );

            AuthConsoleUI authUI = new AuthConsoleUI(authService, scanner);
            UserEntity currentUser = authUI.showAuthMenu();
            if (currentUser == null) {
                System.out.println("Выход.");
                return;
            }

            while (true) {
                System.out.println("\n=== Главное меню ===");
                System.out.println("1. Товары");
                System.out.println("2. Бренды");
                System.out.println("0. Выход");

                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1" -> new ProductConsoleUI(productService, currentUser, scanner).show();
                    case "2" -> new BrandConsoleUI(brandService, currentUser, scanner).show();
                    case "0" -> {
                        auditService.log(currentUser.getUsername(), "выход из системы");
                        System.out.println("До свидания!");
                        return;
                    }
                    default -> System.out.println("Неверный выбор.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
