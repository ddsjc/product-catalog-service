package sukhov.danila.domain.services;

import sukhov.danila.aspect.AuditAction;
import sukhov.danila.domain.entities.*;
import sukhov.danila.domain.exceptions.AlreadyExistsException;
import sukhov.danila.domain.exceptions.AuthenticationException;
import sukhov.danila.domain.exceptions.InvalidInputException;
import sukhov.danila.domain.exceptions.NotFoundException;
import sukhov.danila.domain.repositories.BrandRepository;
import sukhov.danila.domain.repositories.CategoryRepository;
import sukhov.danila.domain.repositories.ProductRepository;
import sukhov.danila.dtos.CreateProductDTO;
import sukhov.danila.dtos.ProductDTO;
import sukhov.danila.dtos.UpdateProductDTO;
import sukhov.danila.mappers.ProductMapper;
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

    public ProductService(
            ProductRepository productRepository,
            BrandRepository brandRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
    }
    @AuditAction("Создал товар")
    public ProductDTO createProduct(CreateProductDTO dto, UserEntity currentUser) {
        validateCreateDto(dto);

        brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new NotFoundException("Бренд с ID " + dto.getBrandId() + " не найден"));
        categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Категория с ID " + dto.getCategoryId() + " не найдена"));

        ProductEntity entity = ProductEntity.builder()
                .name(dto.getName())
                .brandId(dto.getBrandId())
                .categoryId(dto.getCategoryId())
                .price(dto.getPrice())
                .userOwnerId(currentUser.getId())
                .build();

        ProductEntity saved = productRepository.save(entity);
        return ProductMapper.INSTANCE.toDto(saved);
    }
    @AuditAction("Выполнил поиск всех товаров")
    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(ProductMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }
    @AuditAction("Нашел товар по id")
    public ProductDTO findById(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidInputException("Некорректный ID товара");
        }
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Товар с ID " + id + " не найден"));
        return ProductMapper.INSTANCE.toDto(entity);
    }
    @AuditAction("Отредактировал товар")
    public ProductDTO updateProduct(Long id, UpdateProductDTO dto, UserEntity currentUser) {
        if (id == null || id <= 0) {
            throw new InvalidInputException("Некорректный ID товара");
        }
        ProductEntity existing = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Товар не найден"));

        if (!canEdit(currentUser, existing)) {
            throw new AuthenticationException("Нет прав на редактирование этого товара");
        }

        ProductEntity updated = ProductEntity.builder()
                .id(existing.getId())
                .name(dto.getName() != null ? dto.getName() : existing.getName())
                .brandId(dto.getBrandId() != null ? dto.getBrandId() : existing.getBrandId())
                .categoryId(dto.getCategoryId() != null ? dto.getCategoryId() : existing.getCategoryId())
                .price(dto.getPrice() != null ? dto.getPrice() : existing.getPrice())
                .userOwnerId(existing.getUserOwnerId())
                .build();

        ProductEntity saved = productRepository.save(updated);
        return ProductMapper.INSTANCE.toDto(saved);
    }
    @AuditAction("Удалил товар")
    public void deleteProduct(Long id, UserEntity currentUser) {
        ProductEntity existing = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Товар не найден"));

        if (!canEdit(currentUser, existing)) {
            throw new AuthenticationException("Нет прав на удаление этого товара");
        }

        productRepository.deleteById(id);
    }

    private void validateCreateDto(CreateProductDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new InvalidInputException("Название товара обязательно");
        }
        if (dto.getPrice() == null || dto.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("Цена должна быть положительной");
        }
        if (dto.getBrandId() == null || dto.getBrandId() <= 0) {
            throw new InvalidInputException("Некорректный ID бренда");
        }
        if (dto.getCategoryId() == null || dto.getCategoryId()  <= 0) {
            throw new InvalidInputException("Некорректный ID категории");
        }
    }

    private boolean canEdit(UserEntity user, ProductEntity product) {
        return "ADMIN".equals(user.getRole()) ||
                java.util.Objects.equals(user.getId(), product.getUserOwnerId());
    }
}

