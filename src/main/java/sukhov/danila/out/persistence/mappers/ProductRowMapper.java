package sukhov.danila.out.persistence.mappers;

import sukhov.danila.domain.entities.ProductEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper {
    public static ProductEntity productRowMap(ResultSet rs) throws SQLException {
        return ProductEntity.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .categoryId(rs.getLong("category_id"))
                .brandId(rs.getLong("brand_id"))
                .price(rs.getBigDecimal("price"))
                .userOwnerId(rs.getLong("user_owner_id"))
                .build();
    }
}
