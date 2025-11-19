package sukhov.danila.out.persistence.mappers;

import sukhov.danila.domain.entities.BrandEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BrandRowMapper {
    public static BrandEntity brandRowMap(ResultSet rs) throws SQLException {
        return BrandEntity.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .userOwnerId(rs.getLong("user_owner_id"))
                .build();
    }
}
