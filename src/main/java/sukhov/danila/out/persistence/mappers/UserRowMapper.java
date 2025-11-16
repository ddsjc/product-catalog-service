package sukhov.danila.out.persistence.mappers;

import sukhov.danila.domain.entities.UserEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper {
    public static UserEntity userRowMapper(ResultSet rs) throws SQLException {
        return UserEntity.builder()
                .id(rs.getLong("id"))
                .username(rs.getString("username"))
                .passwordHash(rs.getString("password_hash"))
                .role(rs.getString("role"))
                .build();
    }
}
