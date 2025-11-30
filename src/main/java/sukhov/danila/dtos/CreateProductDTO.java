package sukhov.danila.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CreateProductDTO {
    @JsonProperty("name")
    String name;

    @JsonProperty("categoryId")
    Long categoryId;

    @JsonProperty("brandId")
    Long brandId;

    @JsonProperty("price")
    BigDecimal price;

    @JsonCreator
    public CreateProductDTO(
            @JsonProperty("name") String name,
            @JsonProperty("categoryId") Long categoryId,
            @JsonProperty("brandId") Long brandId,
            @JsonProperty("price") BigDecimal price
    ) {
        this.name = name;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.price = price;
    }
}