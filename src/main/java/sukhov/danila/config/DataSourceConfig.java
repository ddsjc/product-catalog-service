package sukhov.danila.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceConfig {
    private static volatile DataSource dataSource;

    public static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DataSourceConfig.class) {
                if (dataSource == null) {
                    HikariConfig config = new HikariConfig();
                    config.setJdbcUrl("jdbc:postgresql://localhost:5433/marketplace_db");
                    config.setUsername("marketplace_user");
                    config.setPassword("secure_password_123");
                    config.setMaximumPoolSize(10);
                    config.setConnectionTimeout(30000);
                    config.setIdleTimeout(600000);
                    config.setMaxLifetime(1800000);
                    dataSource = new HikariDataSource(config);
                }
            }
        }
        return dataSource;
    }
}
