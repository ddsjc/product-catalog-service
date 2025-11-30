package sukhov.danila.domain.services;

import sukhov.danila.aspect.AuditAction;
import sukhov.danila.domain.entities.BrandEntity;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.domain.exceptions.AlreadyExistsException;
import sukhov.danila.domain.repositories.BrandRepository;
import sukhov.danila.dtos.BrandDTO;
import sukhov.danila.mappers.BrandMapper;
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

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @AuditAction("Создал бренд")
    public BrandDTO createBrand(String name, UserEntity currentUser) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название бренда обязательно");
        }
        if (brandRepository.findByName(name).isPresent()) {
            throw new AlreadyExistsException("Бренд '" + name + "' уже существует");
        }

        BrandEntity brand = BrandEntity.builder()
                .name(name)
                .userOwnerId(currentUser.getId())
                .build();

        BrandEntity saved = brandRepository.save(brand);
        return BrandMapper.INSTANCE.toDto(saved);
    }
    @AuditAction("Выполнил поиск всех брендов")
    public java.util.List<BrandDTO> findAllBrands() {
        return brandRepository.findAll().stream()
                .map(BrandMapper.INSTANCE::toDto)
                .collect(java.util.stream.Collectors.toList());
    }
}
