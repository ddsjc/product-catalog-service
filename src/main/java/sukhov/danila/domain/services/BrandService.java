package sukhov.danila.domain.services;

import sukhov.danila.domain.entities.BrandEntity;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.out.repositories.BrandRepository;

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
 *     <li>{@link BrandRepository} — для хранения и проверки уникальности брендов</li>
 *     <li>{@link AuditService} — для записи действий пользователей</li>
 *     <li>{@link Scanner} — для ввода данных через консоль</li>
 * </ul>
 *
 * @author
 *     Данила Сухов
 * @version 1.0
 */
public class BrandService {
    private BrandRepository brandRepository;
    private AuditService auditService;
    private Scanner scanner;

    public BrandService(BrandRepository brandRepository, AuditService auditService, Scanner scanner) {
        this.brandRepository = brandRepository;
        this.auditService = auditService;
        this.scanner = scanner;
    }

    /**
     * Создает новый бренд в системе.
     * <p>Проверяет, существует ли бренд с таким же именем, и при успешном создании фиксирует действие в аудите.</p>
     *
     * @param currentUser текущий пользователь (создатель бренда)
     */
    public void createBrand(UserEntity currentUser) {
        System.out.print("Название бренда: ");
        String name = scanner.nextLine();
        BrandEntity b = new BrandEntity(name, currentUser.getUsername());
        if (brandRepository.addBrand(b)) {
            auditService.log(currentUser.getUsername(), String.format(" Cоздал бренд " + name));
            System.out.println("Бренд добавлен!");
        } else {
            System.out.println("Такой бренд уже существует!");
        }
    }

    /**
     * Выводит список всех существующих брендов.
     * <p>Отображает название и имя пользователя, создавшего каждый бренд.</p>
     */
    public void listBrands() {
        System.out.println("Бренды:");
        for (BrandEntity b : brandRepository.getAllBrands()) {
            System.out.println("- " + b.getName() + " (создатель: " + b.getOwnerUsername() + ")");
        }
    }
}
