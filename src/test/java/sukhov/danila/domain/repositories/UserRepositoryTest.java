package sukhov.danila.domain.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sukhov.danila.BaseIntegrationTest;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.out.persistence.jdbc.UserRepositoryImpl;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.*;

/**
 * Интеграционный тест для UserRepository.
 */
public class UserRepositoryTest extends BaseIntegrationTest {

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
            stmt.execute("ALTER SEQUENCE marketplace.brands_id_seq RESTART WITH 1");
            stmt.execute("ALTER SEQUENCE marketplace.categories_id_seq RESTART WITH 1");
            stmt.execute("ALTER SEQUENCE marketplace.products_id_seq RESTART WITH 1");
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean tables", e);
        }

        userRepository = new UserRepositoryImpl(dataSource);
    }

    /**
     * Проверяет сохранение нового пользователя и генерацию ID.
     */
    @Test
    void shouldSaveNewUserAndAssignId() {
        UserEntity user = new UserEntity(null, "testuser", "secure_hash", "USER");

        UserEntity saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("testuser");
    }

    /**
     * Проверяет поиск пользователя по имени.
     */
    @Test
    void shouldFindUserByUsername() {

        UserEntity user = new UserEntity(null, "danilchik", "hash123", "SELLER");
        userRepository.save(user);

        var found = userRepository.findByName("danilchik");

        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo("SELLER");
    }
}
