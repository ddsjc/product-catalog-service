package sukhov.danila.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class UpdateProductDTO {
    @JsonProperty("name")
    String name;
    @JsonProperty("categoryId")
    Long categoryId;
    @JsonProperty("brandId")
    Long brandId;
    @JsonProperty("price")
    BigDecimal price;

}
