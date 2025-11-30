package sukhov.danila.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class CreateBrandDTO {
    @JsonProperty("name")
    String name;

    @JsonCreator
    public CreateBrandDTO(@JsonProperty("name") String name) {
        this.name = name;
    }
}
