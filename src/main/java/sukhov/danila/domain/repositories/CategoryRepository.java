package sukhov.danila.domain.repositories;

import sukhov.danila.domain.entities.CategoryEntity;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository  {
    CategoryEntity save (CategoryEntity category);
    Optional<CategoryEntity> findById (Long categoryId);
    Optional<CategoryEntity> findByName (String categoryName);
    List<CategoryEntity> findAll();
    void deleteCategoryByName(String categoryName);
    void deleteCategoryById(Long categoryId);
}
