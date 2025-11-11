package sukhov.danila.domain.services;

import sukhov.danila.domain.entities.*;
import sukhov.danila.out.repositories.BrandRepository;
import sukhov.danila.out.repositories.CategoryRepository;
import sukhov.danila.out.repositories.ProductRepository;

import java.util.*;
/**
 * Сервис для управления товарами в системе.
 *
 * <p>Обеспечивает основные операции с товарами, включая:
 * <ul>
 *     <li>Добавление новых товаров с выбором или созданием бренда и категории</li>
 *     <li>Редактирование и удаление существующих товаров с проверкой прав доступа</li>
 *     <li>Поиск по фильтрам (название, бренд, категория, диапазон цен)</li>
 *     <li>Кэширование и аудит действий пользователей</li>
 * </ul>
 *
 * Использует:
 * <ul>
 *     <li>{@link ProductRepository} — хранение и обновление товаров</li>
 *     <li>{@link BrandRepository} — выбор и создание брендов</li>
 *     <li>{@link CategoryRepository} — выбор и создание категорий</li>
 *     <li>{@link AuditService} — журналирование действий</li>
 *     <li>{@link CacheService} — кэширование запросов (опционально)</li>
 * </ul>
 *
 * @author
 *     Данила Сухов
 * @version 1.0
 */
public class ProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final AuditService auditService;
    private final CacheService cacheService;
    private final Scanner scanner;

    public ProductService(ProductRepository productRepository,
                          BrandRepository brandRepository,
                          CategoryRepository categoryRepository,
                          AuditService auditService,
                          CacheService cacheService,
                          Scanner scanner) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.auditService = auditService;
        this.cacheService = cacheService;
        this.scanner = scanner;
    }
    /**
     * Добавляет новый товар в систему.
     *
     * @param currentUser пользователь, выполняющий добавление
     */
    public void addProduct(UserEntity currentUser) {
        System.out.print("Название товара: ");
        String name = scanner.nextLine();

        BrandEntity brand = chooseOrCreateBrand(currentUser);
        CategoryEntity category = chooseOrCreateCategory();

        double price = readDouble("Цена: ", 0);

        ProductEntity p = new ProductEntity(name, category, brand, price, currentUser.getUsername());
        productRepository.addProduct(p);
        auditService.log(currentUser.getUsername(), "добавил товар: " + name);
        System.out.println("Товар добавлен!");
    }
    /**
     * Отображает список всех товаров.
     */
    public void listProducts() {
        System.out.println("Товары:");
        productRepository.getAllProducts().forEach(System.out::println);
    }

    /**
     * Позволяет пользователю изменить данные существующего товара.
     * Проверяет права доступа перед изменением.
     *
     * @param currentUser пользователь, выполняющий редактирование
     */
    public void editProduct(UserEntity currentUser) {
        int id = readInt("ID товара для редактирования: ", 1, Integer.MAX_VALUE);
        ProductEntity p = productRepository.getProduct(id);
        if (p == null) returnError("Товар не найден.");

        if (!hasEditPermission(currentUser, p)) returnError("Нет прав на редактирование этого товара.");

        System.out.println("Оставьте поле пустым, чтобы не изменять.");

        System.out.print("Новое имя (текущее: " + p.getName() + "): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) p.setName(name);

        if (confirm("Изменить бренд? (y/n): "))
            p.setBrand(chooseOrCreateBrand(currentUser));

        if (confirm("Изменить категорию? (y/n): "))
            p.setCategory(chooseOrCreateCategory());

        System.out.print("Новая цена (текущая: " + p.getPrice() + "): ");
        String priceStr = scanner.nextLine();
        if (!priceStr.isEmpty()) p.setPrice(Double.parseDouble(priceStr));

        productRepository.updateProduct(p);
        auditService.log(currentUser.getUsername(), "отредактировал товар: " + p.getName());
        System.out.println("Товар обновлён!");
    }

    /**
     * Удаляет товар по ID.
     *
     * @param currentUser пользователь, выполняющий удаление
     */
    public void removeProduct(UserEntity currentUser) {
        int id = readInt("ID товара для удаления: ", 1, Integer.MAX_VALUE);
        ProductEntity p = productRepository.getProduct(id);
        if (p == null) returnError("Товар не найден.");
        if (!hasEditPermission(currentUser, p)) returnError("Нет прав на удаление этого товара.");

        productRepository.removeProduct(id);
        auditService.log(currentUser.getUsername(), "удалил товар: " + p.getName());
        System.out.println("🗑️ Товар удалён!");
    }

    /**
     * Выполняет поиск товаров по заданным фильтрам.
     */
    public void searchProducts() {
        Collection<ProductEntity> all = productRepository.getAllProducts();
        if (all.isEmpty()) {
            System.out.println("Нет товаров для поиска.");
            return;
        }

        Map<String, Object> filters = new HashMap<>();
        boolean addMore = true;

        while (addMore) {
            System.out.println("\nВыберите критерий поиска:");
            System.out.println("1. По названию");
            System.out.println("2. По бренду");
            System.out.println("3. По категории");
            System.out.println("4. Минимальная цена");
            System.out.println("5. Максимальная цена");
            System.out.println("0. Завершить выбор");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> filters.put("name", readString("Введите название: "));
                case "2" -> filters.put("brand", readString("Введите бренд: "));
                case "3" -> filters.put("category", readString("Введите категорию: "));
                case "4" -> filters.put("minPrice", readDouble("Минимальная цена: ", 0));
                case "5" -> filters.put("maxPrice", readDouble("Максимальная цена: ", 0));
                case "0" -> addMore = false;
                default -> System.out.println("Некорректный выбор.");
            }
        }

        List<ProductEntity> results = applyFilters(all, filters);

        System.out.println("\nРезультаты поиска:");
        if (results.isEmpty())
            System.out.println("Ничего не найдено.");
        else
            results.forEach(System.out::println);
    }

    private BrandEntity chooseOrCreateBrand(UserEntity currentUser) {
        List<BrandEntity> brands = new ArrayList<>(brandRepository.getAllBrands());
        for (int i = 0; i < brands.size(); i++)
            System.out.println((i + 1) + ". " + brands.get(i).getName());
        System.out.println((brands.size() + 1) + ". Создать новый бренд");

        int choice = readInt("Введите номер: ", 1, brands.size() + 1);
        if (choice == brands.size() + 1) {
            String name = readString("Название нового бренда: ");
            BrandEntity brand = new BrandEntity(name, currentUser.getUsername());
            if (!brandRepository.addBrand(brand))
                return brandRepository.findBrandByName(name);
            return brand;
        }
        return brands.get(choice - 1);
    }

    //Вспомогательные методы - общая логика ;)
    private CategoryEntity chooseOrCreateCategory() {
        List<CategoryEntity> categories = new ArrayList<>(categoryRepository.getAllCategories());
        for (int i = 0; i < categories.size(); i++)
            System.out.println((i + 1) + ". " + categories.get(i).getName());
        System.out.println((categories.size() + 1) + ". Создать новую категорию");

        int choice = readInt("Ваш выбор: ", 1, categories.size() + 1);
        if (choice == categories.size() + 1) {
            String name = readString("Название новой категории: ");
            CategoryEntity c = new CategoryEntity(name);
            categoryRepository.addCategory(c);
            return c;
        }
        return categories.get(choice - 1);
    }

    private boolean hasEditPermission(UserEntity user, ProductEntity p) {
        return user.getRole().equals(ERole.ADMIN) ||
                p.getBrand().getOwnerUsername().equals(user.getUsername());
    }

    private List<ProductEntity> applyFilters(Collection<ProductEntity> products, Map<String, Object> filters) {
        List<ProductEntity> result = new ArrayList<>();
        for (ProductEntity p : products) {
            if (filters.containsKey("name") && !p.getName().toLowerCase()
                    .contains(((String) filters.get("name")).toLowerCase())) continue;
            if (filters.containsKey("brand") && !p.getBrand().getName()
                    .equalsIgnoreCase((String) filters.get("brand"))) continue;
            if (filters.containsKey("category") && !p.getCategory().getName()
                    .equalsIgnoreCase((String) filters.get("category"))) continue;
            if (filters.containsKey("minPrice") && p.getPrice() < (Double) filters.get("minPrice")) continue;
            if (filters.containsKey("maxPrice") && p.getPrice() > (Double) filters.get("maxPrice")) continue;
            result.add(p);
        }
        return result;
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine());
                if (value >= min && value <= max) return value;
                System.out.println("Число вне диапазона (" + min + "–" + max + ")");
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число!");
            }
        }
    }

    private double readDouble(String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine());
                if (value >= min) return value;
                System.out.println("Число должно быть ≥ " + min);
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число!");
            }
        }
    }

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private boolean confirm(String message) {
        System.out.print(message);
        String ans = scanner.nextLine().trim().toLowerCase();
        return ans.equals("y") || ans.equals("д");
    }

    private void returnError(String message) {
        System.out.println("" + message);
        throw new RuntimeException(message);
    }
}

