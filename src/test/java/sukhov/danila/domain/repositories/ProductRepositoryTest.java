package sukhov.danila.domain.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import sukhov.danila.domain.entities.ProductEntity;
import sukhov.danila.out.persistence.jdbc.ProductRepositoryImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class ProductRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("test_marketplace")
                    .withUsername("test_user")
                    .withPassword("test_pass");

    private Connection connection;
    private ProductRepository productRepository;
    private UserRepository userRepository;
    private BrandRepository brandRepository;
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() throws Exception {
        connection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        connection.createStatement().execute("CREATE SCHEMA IF NOT EXISTS marketplace");

        connection.createStatement().execute(
                "CREATE TABLE marketplace.users (" +
                        "id BIGSERIAL PRIMARY KEY, " +
                        "username VARCHAR(100) NOT NULL UNIQUE, " +
                        "password_hash VARCHAR(255) NOT NULL, " +
                        "role VARCHAR(20) NOT NULL)"
        );
        connection.createStatement().execute(
                "CREATE TABLE marketplace.categories (" +
                        "id BIGSERIAL PRIMARY KEY, " +
                        "name VARCHAR(100) NOT NULL UNIQUE)"
        );
        connection.createStatement().execute(
                "CREATE TABLE marketplace.brands (" +
                        "id BIGSERIAL PRIMARY KEY, " +
                        "name VARCHAR(100) NOT NULL UNIQUE, " +
                        "user_owner_id BIGINT NOT NULL, " +
                        "FOREIGN KEY (user_owner_id) REFERENCES marketplace.users(id))"
        );
        connection.createStatement().execute(
                "CREATE TABLE marketplace.products (" +
                        "id BIGSERIAL PRIMARY KEY, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "category_id BIGINT NOT NULL, " +
                        "brand_id BIGINT NOT NULL, " +
                        "price DECIMAL(19,2) NOT NULL, " +
                        "user_owner_id BIGINT NOT NULL, " +
                        "FOREIGN KEY (category_id) REFERENCES marketplace.categories(id), " +
                        "FOREIGN KEY (brand_id) REFERENCES marketplace.brands(id), " +
                        "FOREIGN KEY (user_owner_id) REFERENCES marketplace.users(id))"
        );

        connection.createStatement().execute(
                "INSERT INTO marketplace.users (username, password_hash, role) " +
                        "VALUES ('owner', 'hash', 'SELLER')"
        );
        connection.createStatement().execute(
                "INSERT INTO marketplace.categories (name) " +
                        "VALUES ('Electronics')"
        );
        connection.createStatement().execute(
                "INSERT INTO marketplace.brands (name, user_owner_id) " +
                        "VALUES ('TestBrand', 1)"
        );

        productRepository = new ProductRepositoryImpl(connection);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void shouldSaveAndFindProduct() {

        ProductEntity product = ProductEntity.builder()
                .name("Test Laptop")
                .categoryId(1L)
                .brandId(1L)
                .price(new BigDecimal("999.99"))
                .userOwnerId(1L)
                .build();


        ProductEntity saved = productRepository.save(product);
        var found = productRepository.findById(saved.getId());


        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Laptop");
        assertThat(found.get().getPrice()).isEqualByComparingTo("999.99");
    }
}
