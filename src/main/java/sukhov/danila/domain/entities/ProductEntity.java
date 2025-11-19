package sukhov.danila.domain.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
public class ProductEntity implements Serializable {
    @EqualsAndHashCode.Include
    Long id;
    String name;
    Long categoryId;
    Long brandId;
    BigDecimal price;
    Long userOwnerId;

    public ProductEntity(String name, Long categoryId, Long brandId, BigDecimal price, Long userOwnerId) {
        this.name = name;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.price = price;
        this.userOwnerId = userOwnerId;
    }

}
