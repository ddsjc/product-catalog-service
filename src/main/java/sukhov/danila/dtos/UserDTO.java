package sukhov.danila.dtos;

import lombok.*;
@Value
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
public class UserDTO {
    Long id;
    String username;
    String passwordHash;
    String role;
}
