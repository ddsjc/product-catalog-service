package sukhov.danila.out.persistence.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.domain.repositories.UserRepository;
import sukhov.danila.out.persistence.mappers.UserRowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final DataSource dataSource;

    public UserRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public UserEntity save(UserEntity user) {
        if (user.getId() == null) {
            String sql = "INSERT INTO marketplace.users (username, password_hash, role) VALUES (?, ?, ?)";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPasswordHash());
                stmt.setString(3, user.getRole());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setId(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Не удалось сохранить пользователя", e);
            }
        } else {
            String sql = "UPDATE marketplace.users SET username = ?, password_hash = ?, role = ? WHERE id = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPasswordHash());
                stmt.setString(3, user.getRole());
                stmt.setLong(4, user.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Не удалось обновить пользователя", e);
            }
        }
        return user;
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        String sql = "SELECT id, username, password_hash, role FROM marketplace.users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(UserRowMapper.userRowMapper(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска пользователя по ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserEntity> findByName(String username) {
        String sql = "SELECT id, username, password_hash, role FROM marketplace.users WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(UserRowMapper.userRowMapper(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска пользователя по имени", e);
        }
        return Optional.empty();
    }

    @Override
    public List<UserEntity> findAll() {
        String sql = "SELECT id, username, password_hash, role FROM marketplace.users";
        List<UserEntity> users = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(UserRowMapper.userRowMapper(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка загрузки пользователей", e);
        }
        return users;
    }

    @Override
    public void deleteUserById(Long id) {
        String sql = "DELETE FROM marketplace.users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления пользователя", e);
        }
    }
}

