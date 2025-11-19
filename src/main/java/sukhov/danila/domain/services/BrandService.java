package sukhov.danila.domain.services;

import sukhov.danila.domain.entities.BrandEntity;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.domain.exceptions.AlreadyExistsException;
import sukhov.danila.domain.repositories.BrandRepository;
import sukhov.danila.out.persistence.jdbc.BrandRepositoryImpl;

import java.util.Scanner;

/**
 * Сервис для управления брендами в системе.
 *
 * <p>Класс предоставляет функциональность для:
 * <ul>
 *     <li>Создания новых брендов (администратором или продавцом)</li>
 *     <li>Вывода списка всех существующих брендов</li>
 *     <li>Выполнения аудита действий пользователей</li>
 * </ul>
 *
 * <p>Бренды являются уникальными — попытка создать бренд с уже существующим именем будет отклонена.</p>
 *
 * Использует:
 * <ul>
 *     <li>{@link BrandRepositoryImpl} — для хранения и проверки уникальности брендов</li>
 *     <li>{@link AuditService} — для записи действий пользователей</li>
 *     <li>{@link Scanner} — для ввода данных через консоль</li>
 * </ul>
 *
 * @author
 *     Данила Сухов
 * @version 1.0
 */
public class BrandService {
    private final BrandRepository brandRepository;
    private final AuditService auditService;
    private final Scanner scanner;

    public BrandService(BrandRepository brandRepository, AuditService auditService, Scanner scanner) {
        this.brandRepository = brandRepository;
        this.auditService = auditService;
        this.scanner = scanner;
    }

    public BrandEntity createBrand(UserEntity currentUser) {
        System.out.print("Название бренда: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Название бренда не может быть пустым");
        }
        if (brandRepository.findByName(name).isPresent()) {
            throw new AlreadyExistsException("Бренд '" + name + "' уже существует");
        }
        BrandEntity brand = BrandEntity.builder()
                .name(name)
                .userOwnerId(currentUser.getId())
                .build();
        brand = brandRepository.save(brand);
        auditService.log(currentUser.getUsername(), "создал бренд: " + name);
        System.out.println("Бренд успешно добавлен!");
        return brand;
    }

    public void listBrands() {
        var brands = brandRepository.findAll();
        if (brands.isEmpty()) {
            System.out.println("Нет брендов.");
            return;
        }
        System.out.println("Бренды:");
        brands.forEach(b -> System.out.println("- " + b.getName() + " (владелец: " + b.getUserOwnerId() + ")"));
    }
}
