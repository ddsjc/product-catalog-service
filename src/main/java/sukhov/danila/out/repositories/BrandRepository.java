package sukhov.danila.out.repositories;

import sukhov.danila.out.storage.FileStorage;
import sukhov.danila.domain.entities.BrandEntity;

import java.util.HashSet;
import java.util.Set;

public class BrandRepository {
    private final String file = "brands.ser";
    private final Set<BrandEntity> brands;

    public BrandRepository() {
        brands = new HashSet<>(FileStorage.loadCollection(file));
    }

    public boolean addBrand(BrandEntity brand) {
        boolean added = brands.add(brand);
        if (added) FileStorage.saveCollection(file, brands);
        return added;
    }

    public Set<BrandEntity> getAllBrands() {
        return brands;
    }

    public BrandEntity findBrandByName(String name) {
        return brands.stream()
                .filter(b -> b.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
