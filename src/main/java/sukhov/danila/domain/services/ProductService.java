package sukhov.danila.domain.services;

import sukhov.danila.domain.entities.*;
import sukhov.danila.domain.exceptions.AlreadyExistsException;
import sukhov.danila.domain.exceptions.AuthenticationException;
import sukhov.danila.domain.exceptions.NotFoundException;
import sukhov.danila.domain.repositories.BrandRepository;
import sukhov.danila.domain.repositories.CategoryRepository;
import sukhov.danila.domain.repositories.ProductRepository;
import sukhov.danila.out.persistence.jdbc.BrandRepositoryImpl;
import sukhov.danila.out.persistence.jdbc.CategoryRepositoryImpl;
import sukhov.danila.out.persistence.jdbc.ProductRepositoryImpl;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
 *     <li>{@link ProductRepositoryImpl} — хранение и обновление товаров</li>
 *     <li>{@link BrandRepositoryImpl} — выбор и создание брендов</li>
 *     <li>{@link CategoryRepositoryImpl} — выбор и создание категорий</li>
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
    private final Scanner scanner;
    private final BrandService brandService;
    private final CacheService cacheService;

    public ProductService(
            ProductRepository productRepository,
            BrandRepository brandRepository,
            CategoryRepository categoryRepository,
            AuditService auditService,
            Scanner scanner,
            BrandService brandService,
            CacheService cacheService) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.auditService = auditService;
        this.scanner = scanner;
        this.brandService = brandService;
        this.cacheService = cacheService;
    }

    public void addProduct(UserEntity currentUser) {
        System.out.print("Название товара: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Название не может быть пустым.");
            return;
        }

        BrandEntity brand = chooseOrCreateBrand(currentUser);
        CategoryEntity category = chooseOrCreateCategory();
        BigDecimal price = readDecimal("Цена: ", BigDecimal.ZERO);

        ProductEntity product = ProductEntity.builder()
                .name(name)
                .brandId(brand.getId())
                .categoryId(category.getId())
                .price(price)
                .userOwnerId(currentUser.getId())
                .build();

        product = productRepository.save(product);
        invalidateCache();
        auditService.log(currentUser.getUsername(), "добавил товар: " + name);
        System.out.println("Товар успешно добавлен!");
    }

    public void listProducts() {
        List<ProductEntity> products = productRepository.findAll();
        if (products.isEmpty()) {
            System.out.println("Каталог пуст.");
            return;
        }
        System.out.println("Товары:");
        products.forEach(System.out::println);
    }

    public void editProduct(UserEntity currentUser) {
        Long id = readLong("ID товара для редактирования: ");
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Товар с ID " + id + " не найден"));

        if (!canEdit(currentUser, product)) {
            throw new AuthenticationException("Вы не являетесь владельцем и не можете редактировать этот товар.");
        }

        System.out.println("Текущие данные: " + product);
        System.out.println("Оставьте поле пустым, чтобы не изменять.");

        System.out.print("Новое название: ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) product.setName(name);

        if (confirm("Изменить бренд? (y/n): ")) {
            product.setBrandId(chooseOrCreateBrand(currentUser).getId());
        }

        if (confirm("Изменить категорию? (y/n): ")) {
            product.setCategoryId(chooseOrCreateCategory().getId());
        }

        System.out.print("Новая цена: ");
        String priceStr = scanner.nextLine().trim();
        if (!priceStr.isEmpty()) {
            try {
                product.setPrice(new BigDecimal(priceStr));
            } catch (NumberFormatException e) {
                System.out.println("Некорректная цена. Изменение цены отменено.");
            }
        }

        productRepository.save(product);
        invalidateCache();
        auditService.log(currentUser.getUsername(), "отредактировал товар: " + product.getName());
        System.out.println("Товар обновлён!");
    }

    public void removeProduct(UserEntity currentUser) {
        Long id = readLong("ID товара для удаления: ");
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Товар с ID " + id + " не найден"));

        if (!canEdit(currentUser, product)) {
            throw new AuthenticationException("Вы не являетесь владельцем и не можете удалить этот товар.");
        }

        productRepository.deleteById(id);
        invalidateCache();
        auditService.log(currentUser.getUsername(), "удалил товар: " + product.getName());
        System.out.println("Товар удалён.");
    }

    public void searchProducts() {
        List<ProductEntity> all = productRepository.findAll();
        if (all.isEmpty()) {
            System.out.println("Нет товаров для поиска.");
            return;
        }

        Map<String, Object> filters = new HashMap<>();
        boolean addMore = true;

        while (addMore) {
            System.out.println("\nКритерии поиска:");
            System.out.println("1. Название");
            System.out.println("2. Бренд");
            System.out.println("3. Категория");
            System.out.println("4. Мин. цена");
            System.out.println("5. Макс. цена");
            System.out.println("0. Завершить");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> filters.put("name", readString("Название: "));
                case "2" -> {
                    String brandName = readString("Бренд: ");
                    brandRepository.findByName(brandName)
                            .ifPresentOrElse(
                                    brand -> filters.put("brandId", brand.getId()),
                                    () -> System.out.println("Бренд не найден. Фильтр не применён.")
                            );
                }
                case "3" -> {
                    String categoryName = readString("Категория: ");
                    categoryRepository.findByName(categoryName)
                            .ifPresentOrElse(
                                    category -> filters.put("categoryId", category.getId()),
                                    () -> System.out.println("Категория не найдена. Фильтр не применён.")
                            );
                }
                case "4" -> filters.put("minPrice", readDecimal("Мин. цена: ", BigDecimal.ZERO));
                case "5" -> filters.put("maxPrice", readDecimal("Макс. цена: ", BigDecimal.ZERO));
                case "0" -> addMore = false;
                default -> System.out.println("Неверный выбор.");
            }
        }

        String cacheKey = buildCacheKey(filters);
        List<ProductEntity> results;
        if (cacheService.contains(cacheKey)) {
            System.out.println("(Результат из кэша)");
            results = cacheService.get(cacheKey);
        } else {
            results = applyFilters(all, filters);
            cacheService.put(cacheKey, results);
        }

        System.out.println("\nРезультаты поиска (" + results.size() + "):");
        if (results.isEmpty()) {
            System.out.println("Ничего не найдено.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private void invalidateCache() {
        cacheService.clear();
    }

    private String buildCacheKey(Map<String, Object> filters) {
        return filters.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
    }

    private List<ProductEntity> applyFilters(List<ProductEntity> products, Map<String, Object> filters) {
        return products.stream().filter(p -> {
            if (filters.containsKey("name")) {
                String nameFilter = ((String) filters.get("name")).toLowerCase();
                if (!p.getName().toLowerCase().contains(nameFilter)) return false;
            }
            if (filters.containsKey("brandId")) {
                Long brandId = (Long) filters.get("brandId");
                if (!Objects.equals(p.getBrandId(), brandId)) return false;
            }
            if (filters.containsKey("categoryId")) {
                Long categoryId = (Long) filters.get("categoryId");
                if (!Objects.equals(p.getCategoryId(), categoryId)) return false;
            }
            if (filters.containsKey("minPrice")) {
                BigDecimal min = (BigDecimal) filters.get("minPrice");
                if (p.getPrice().compareTo(min) < 0) return false;
            }
            if (filters.containsKey("maxPrice")) {
                BigDecimal max = (BigDecimal) filters.get("maxPrice");
                if (p.getPrice().compareTo(max) > 0) return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    private BrandEntity chooseOrCreateBrand(UserEntity currentUser) {
        List<BrandEntity> brands = brandRepository.findAll();
        for (int i = 0; i < brands.size(); i++) {
            System.out.println((i + 1) + ". " + brands.get(i).getName());
        }
        System.out.println((brands.size() + 1) + ". Создать новый бренд");

        int choice = readInt("Выберите бренд: ", 1, brands.size() + 1);
        if (choice == brands.size() + 1) {
            return brandService.createBrand(currentUser);
        }
        return brands.get(choice - 1);
    }

    private CategoryEntity chooseOrCreateCategory() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i).getName());
        }
        System.out.println((categories.size() + 1) + ". Создать новую категорию");

        int choice = readInt("Выберите категорию: ", 1, categories.size() + 1);
        if (choice == categories.size() + 1) {
            String name = readString("Название категории: ");
            if (categoryRepository.findByName(name).isPresent()) {
                throw new AlreadyExistsException("Категория '" + name + "' уже существует");
            }
            return categoryRepository.save(CategoryEntity.builder().name(name).build());
        }
        return categories.get(choice - 1);
    }

    private boolean canEdit(UserEntity user, ProductEntity product) {
        return ERole.ADMIN.name().equals(user.getRole()) ||
                Objects.equals(user.getId(), product.getUserOwnerId());
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) return value;
                System.out.println("Выберите число от " + min + " до " + max);
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число.");
            }
        }
    }

    private long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Введите корректный ID.");
            }
        }
    }

    private BigDecimal readDecimal(String prompt, BigDecimal min) {
        while (true) {
            System.out.print(prompt);
            try {
                BigDecimal value = new BigDecimal(scanner.nextLine().trim());
                if (value.compareTo(min) >= 0) return value;
                System.out.println("Значение должно быть ≥ " + min);
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число.");
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
        return ans.equals("y") || ans.equals("д") || ans.equals("yes");
    }
}

