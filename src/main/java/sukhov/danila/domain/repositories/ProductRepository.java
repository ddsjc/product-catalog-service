package sukhov.danila.domain.repositories;

import sukhov.danila.domain.entities.ProductEntity;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    ProductEntity save(ProductEntity product);
    Optional<ProductEntity> findById(Long id);
    Optional<ProductEntity> findByName(String name);
    List<ProductEntity> findAll();
    void deleteById(Long id);
}
