package sukhov.danila.dtos;

import lombok.*;

@Value
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
public class BrandDTO {
    Long id;
    String name;
    Long userOwnerId;
}
