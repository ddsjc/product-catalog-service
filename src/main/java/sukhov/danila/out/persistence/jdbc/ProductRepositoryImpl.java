// sukhov.danila.out.persistence.jdbc.ProductRepositoryImpl
package sukhov.danila.out.persistence.jdbc;

import org.springframework.stereotype.Repository;
import sukhov.danila.domain.entities.ProductEntity;
import sukhov.danila.domain.repositories.ProductRepository;
import sukhov.danila.out.persistence.mappers.ProductRowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final DataSource dataSource;

    public ProductRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ProductEntity save(ProductEntity product) {
        if (product.getId() == null) {
            String sql = "INSERT INTO marketplace.products (name, category_id, brand_id, price, user_owner_id) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, product.getName());
                stmt.setLong(2, product.getCategoryId());
                stmt.setLong(3, product.getBrandId());
                stmt.setBigDecimal(4, product.getPrice());
                stmt.setLong(5, product.getUserOwnerId());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        product.setId(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Не удалось сохранить товар", e);
            }
        } else {
            String sql = "UPDATE marketplace.products SET name = ?, category_id = ?, brand_id = ?, price = ?, user_owner_id = ? WHERE id = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, product.getName());
                stmt.setLong(2, product.getCategoryId());
                stmt.setLong(3, product.getBrandId());
                stmt.setBigDecimal(4, product.getPrice());
                stmt.setLong(5, product.getUserOwnerId());
                stmt.setLong(6, product.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Не удалось обновить товар", e);
            }
        }
        return product;
    }

    @Override
    public Optional<ProductEntity> findById(Long id) {
        String sql = "SELECT id, name, category_id, brand_id, price, user_owner_id FROM marketplace.products WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(ProductRowMapper.productRowMap(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска товара по ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ProductEntity> findByName(String name) {
        String sql = "SELECT id, name, category_id, brand_id, price, user_owner_id FROM marketplace.products WHERE name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(ProductRowMapper.productRowMap(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска товара по имени", e);
        }
        return Optional.empty();
    }

    @Override
    public List<ProductEntity> findAll() {
        String sql = "SELECT id, name, category_id, brand_id, price, user_owner_id FROM marketplace.products";
        List<ProductEntity> products = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                products.add(ProductRowMapper.productRowMap(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка загрузки товаров", e);
        }
        return products;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM marketplace.products WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления товара", e);
        }
    }
}