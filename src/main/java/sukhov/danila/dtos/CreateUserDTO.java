package sukhov.danila.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
@Value
@Builder
public class CreateUserDTO {
    @JsonProperty("username")
    String username;
    @JsonProperty("password")
    String password;
    @JsonProperty("role")
    String role;

    @JsonCreator
    public CreateUserDTO(
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("role") String role
    ) {
        this.username = username;
        this.password = password;
        this. role = role;
    }
}
