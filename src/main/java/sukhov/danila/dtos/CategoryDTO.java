package sukhov.danila.dtos;

import lombok.*;
@Value
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
public class CategoryDTO {
    Long id;
    String name;
}
