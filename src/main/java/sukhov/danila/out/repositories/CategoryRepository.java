package sukhov.danila.out.repositories;

import sukhov.danila.out.storage.FileStorage;
import sukhov.danila.domain.entities.CategoryEntity;

import java.util.HashSet;
import java.util.Set;

public class CategoryRepository {
    private final String file = "categories.ser";
    private final Set<CategoryEntity> categories;

    public CategoryRepository() {
        categories = new HashSet<>(FileStorage.loadCollection(file));
    }

    public boolean addCategory(CategoryEntity category) {
        boolean added = categories.add(category);
        if (added) FileStorage.saveCollection(file, categories);
        return added;
    }

    public Set<CategoryEntity> getAllCategories() {
        return categories;
    }

    public CategoryEntity findByName(String name) {
        return categories.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
