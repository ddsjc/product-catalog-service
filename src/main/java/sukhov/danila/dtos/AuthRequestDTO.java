package sukhov.danila.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
public class AuthRequestDTO {
    @JsonProperty("username")
    String username;
    @JsonProperty("password")
    String password;
    @JsonProperty("role")
    String role;
    @JsonCreator
    public AuthRequestDTO(@JsonProperty("username") String username, @JsonProperty("password") String password,@JsonProperty("role") String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
