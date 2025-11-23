package sukhov.danila.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;

import java.math.BigDecimal;
@Value
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
public class ProductDTO {
    Long id;
    String name;
    Long categoryId;
    Long brandId;
    BigDecimal price;
    Long userOwnerId;

}
