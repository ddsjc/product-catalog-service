package sukhov.danila;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.sql.Connection;

public abstract class BaseIntegrationTest {

    protected static PostgreSQLContainer<?> postgres;
    protected static DataSource dataSource;

    static {
        try {
            postgres = new PostgreSQLContainer<>(
                    DockerImageName.parse("postgres:15").asCompatibleSubstituteFor("postgres")
            )
                    .withDatabaseName("test_marketplace")
                    .withUsername("test_user")
                    .withPassword("test_pass");

            postgres.start();
            dataSource = createDataSource();
            runLiquibaseMigrations();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize test container", e);
        }
    }

    @AfterAll
    static void tearDown() {
        if (dataSource instanceof AutoCloseable) {
            try {
                ((AutoCloseable) dataSource).close();
            } catch (Exception ignore) {}
        }
        if (postgres != null) {
            postgres.stop();
        }
    }

    private static DataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(postgres.getUsername());
        config.setPassword(postgres.getPassword());
        config.setMaximumPoolSize(5);
        return new HikariDataSource(config);
    }

    private static void runLiquibaseMigrations() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().execute("CREATE SCHEMA IF NOT EXISTS marketplace");
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.yaml",
                    new ClassLoaderResourceAccessor(),
                    database
            );
            liquibase.update(new Contexts(), new LabelExpression());
        }
    }
}
