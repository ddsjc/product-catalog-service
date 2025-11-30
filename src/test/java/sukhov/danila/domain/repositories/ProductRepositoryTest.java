package sukhov.danila.domain.repositories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sukhov.danila.BaseIntegrationTest;
import sukhov.danila.domain.entities.BrandEntity;
import sukhov.danila.domain.entities.CategoryEntity;
import sukhov.danila.domain.entities.ProductEntity;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.out.persistence.jdbc.*;

import java.math.BigDecimal;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.*;
public class ProductRepositoryTest extends BaseIntegrationTest{
    private ProductRepository productRepository;
    private BrandRepository brandRepository;
    private CategoryRepository categoryRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        try (Connection conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM marketplace.products");
            stmt.execute("DELETE FROM marketplace.brands");
            stmt.execute("DELETE FROM marketplace.categories");
            stmt.execute("DELETE FROM marketplace.users");

            stmt.execute("ALTER SEQUENCE marketplace.users_id_seq RESTART WITH 1");
            stmt.execute("ALTER SEQUENCE marketplace.categories_id_seq RESTART WITH 1");
            stmt.execute("ALTER SEQUENCE marketplace.brands_id_seq RESTART WITH 1");
            stmt.execute("ALTER SEQUENCE marketplace.products_id_seq RESTART WITH 1");
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean tables", e);
        }

        productRepository = new ProductRepositoryImpl(dataSource);
        brandRepository = new BrandRepositoryImpl(dataSource);
        categoryRepository = new CategoryRepositoryImpl(dataSource);
        userRepository = new UserRepositoryImpl(dataSource);
    }

    /**
     * Проверяет сохранение нового товара и генерацию ID.
     */
    @Test
    void shouldSaveNewProductAndAssignId() {
        UserEntity owner = new UserEntity(null, "owner", "hash", "SELLER");
        owner = userRepository.save(owner);

        CategoryEntity category = new CategoryEntity(null, "Electronics");
        category = categoryRepository.save(category);

        BrandEntity brand = new BrandEntity(null, "Apple", owner.getId());
        brand = brandRepository.save(brand);

        ProductEntity product = new ProductEntity(
                null, "iPhone", category.getId(), brand.getId(), BigDecimal.valueOf(999.99), owner.getId()
        );

        ProductEntity saved = productRepository.save(product);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("iPhone");
        assertThat(saved.getPrice()).isEqualByComparingTo("999.99");
    }

    /**
     * Проверяет поиск товара по ID.
     */
    @Test
    void shouldFindProductById() {
        UserEntity owner = new UserEntity(null, "owner", "hash", "SELLER");
        owner = userRepository.save(owner);

        CategoryEntity category = new CategoryEntity(null, "Books");
        category = categoryRepository.save(category);

        BrandEntity brand = new BrandEntity(null, "O'Reilly", owner.getId());
        brand = brandRepository.save(brand);

        ProductEntity product = new ProductEntity(
                null, "Learning Java", category.getId(), brand.getId(), BigDecimal.valueOf(49.99), owner.getId()
        );
        product = productRepository.save(product);

        var found = productRepository.findById(product.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Learning Java");
    }
}
