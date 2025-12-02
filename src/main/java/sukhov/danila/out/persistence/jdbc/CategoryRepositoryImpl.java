package sukhov.danila.out.persistence.jdbc;


import org.springframework.stereotype.Repository;
import sukhov.danila.domain.repositories.CategoryRepository;
import sukhov.danila.out.persistence.mappers.CategoryRowMapper;
import sukhov.danila.domain.entities.CategoryEntity;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final DataSource dataSource;

    public CategoryRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CategoryEntity save(CategoryEntity category) {
        if (category.getId() == null) {
            String sql = "INSERT INTO marketplace.categories (name) VALUES (?)";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, category.getName());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        category.setId(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Не удалось сохранить категорию", e);
            }
        } else {
            String sql = "UPDATE marketplace.categories SET name = ? WHERE id = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, category.getName());
                stmt.setLong(2, category.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Не удалось обновить категорию", e);
            }
        }
        return category;
    }

    @Override
    public Optional<CategoryEntity> findById(Long id) {
        String sql = "SELECT id, name FROM marketplace.categories WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(CategoryRowMapper.categoryRowMap(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска категории по ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<CategoryEntity> findByName(String name) {
        String sql = "SELECT id, name FROM marketplace.categories WHERE name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(CategoryRowMapper.categoryRowMap(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска категории по имени", e);
        }
        return Optional.empty();
    }

    @Override
    public List<CategoryEntity> findAll() {
        String sql = "SELECT id, name FROM marketplace.categories";
        List<CategoryEntity> categories = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(CategoryRowMapper.categoryRowMap(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка загрузки категорий", e);
        }
        return categories;
    }

    @Override
    public void deleteCategoryByName(String name) {
        String sql = "DELETE FROM marketplace.categories WHERE name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления категории по имени", e);
        }
    }

    @Override
    public void deleteCategoryById(Long id) {
        String sql = "DELETE FROM marketplace.categories WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления категории по ID", e);
        }
    }
}

