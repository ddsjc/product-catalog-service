package sukhov.danila.out.persistence.mappers;

import sukhov.danila.domain.entities.CategoryEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryRowMapper {
    public static CategoryEntity categoryRowMap(ResultSet rs) throws SQLException {
        return CategoryEntity.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }
}
