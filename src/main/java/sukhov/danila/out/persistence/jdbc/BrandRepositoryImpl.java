package sukhov.danila.out.persistence.jdbc;

import sukhov.danila.domain.repositories.BrandRepository;
import sukhov.danila.domain.entities.BrandEntity;
import sukhov.danila.out.persistence.mappers.BrandRowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BrandRepositoryImpl implements BrandRepository {

    private final DataSource dataSource;

    public BrandRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public BrandEntity save(BrandEntity brand) {
        if (brand.getId() == null) {
            String sql = "INSERT INTO marketplace.brands (name, user_owner_id) VALUES (?, ?)";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, brand.getName());
                stmt.setLong(2, brand.getUserOwnerId());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        brand.setId(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Не удалось сохранить бренд", e);
            }
        } else {
            String sql = "UPDATE marketplace.brands SET name = ?, user_owner_id = ? WHERE id = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, brand.getName());
                stmt.setLong(2, brand.getUserOwnerId());
                stmt.setLong(3, brand.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Не удалось обновить бренд", e);
            }
        }
        return brand;
    }

    @Override
    public Optional<BrandEntity> findById(Long id) {
        String sql = "SELECT id, name, user_owner_id FROM marketplace.brands WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(BrandRowMapper.brandRowMap(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска бренда по ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<BrandEntity> findByName(String name) {
        String sql = "SELECT id, name, user_owner_id FROM marketplace.brands WHERE name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(BrandRowMapper.brandRowMap(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска бренда по имени", e);
        }
        return Optional.empty();
    }

    @Override
    public List<BrandEntity> findAll() {
        String sql = "SELECT id, name, user_owner_id FROM marketplace.brands";
        List<BrandEntity> brands = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                brands.add(BrandRowMapper.brandRowMap(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка загрузки брендов", e);
        }
        return brands;
    }

    @Override
    public void deleteBrandById(Long id) {
        String sql = "DELETE FROM marketplace.brands WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления бренда", e);
        }
    }
}

