package sukhov.danila.domain.repositories;

import java.sql.Connection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.out.persistence.jdbc.UserRepositoryImpl;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
public class UserRepositoryTest {
    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("test_marketplace")
                    .withUsername("test_user")
                    .withPassword("test_pass");

    private Connection connection;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        connection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        connection.createStatement().execute("CREATE SCHEMA IF NOT EXISTS marketplace");
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS marketplace.users (" +
                        "id BIGSERIAL PRIMARY KEY, " +
                        "username VARCHAR(100) NOT NULL UNIQUE, " +
                        "password_hash VARCHAR(255) NOT NULL, " +
                        "role VARCHAR(20) NOT NULL)"
        );

        userRepository = new UserRepositoryImpl(connection);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void shouldSaveNewUserAndAssignId() {

        UserEntity user = new UserEntity(null, "testuser", "secure_hash", "USER");

        UserEntity saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("testuser");
    }

    @Test
    void shouldFindUserByUsername() {
        UserEntity user = new UserEntity(null, "danilchik", "hash123", "SELLER");
        userRepository.save(user);

        var found = userRepository.findByName("danilchik");

        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo("SELLER");
    }
}
