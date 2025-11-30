package sukhov.danila.domain.repositories;

import org.springframework.stereotype.Repository;
import sukhov.danila.domain.entities.CategoryEntity;

import java.util.List;
import java.util.Optional;
@Repository
public interface CategoryRepository  {
    CategoryEntity save (CategoryEntity category);
    Optional<CategoryEntity> findById (Long categoryId);
    Optional<CategoryEntity> findByName (String categoryName);
    List<CategoryEntity> findAll();
    void deleteCategoryByName(String categoryName);
    void deleteCategoryById(Long categoryId);
}
