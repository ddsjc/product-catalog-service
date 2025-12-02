package sukhov.danila.domain.repositories;

import org.springframework.stereotype.Repository;
import sukhov.danila.domain.entities.BrandEntity;

import java.util.List;
import java.util.Optional;
@Repository
public interface BrandRepository {
    BrandEntity save (BrandEntity brand);
    Optional<BrandEntity> findById (Long brandId);
    Optional<BrandEntity> findByName (String name);
    List<BrandEntity> findAll();
    void deleteBrandById(Long id);
}
