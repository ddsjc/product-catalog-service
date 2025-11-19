package sukhov.danila.domain.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
public class BrandEntity implements Serializable {

    @EqualsAndHashCode.Include
    Long id;
    String name;
    Long userOwnerId;

}
