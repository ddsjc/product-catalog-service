package sukhov.danila.out.repositories;

import sukhov.danila.out.storage.FileStorage;
import sukhov.danila.domain.entities.ProductEntity;

import java.util.*;

public class ProductRepository {
    private final String file = "products.ser";
    private final Map<Integer, ProductEntity> products;

    public ProductRepository() {
        products = FileStorage.loadMap(file);
    }

    public void addProduct(ProductEntity product) {
        products.put(product.getId(), product);
        FileStorage.saveMap(file, products);
    }

    public void removeProduct(int id) {
        products.remove(id);
        FileStorage.saveMap(file, products);
    }

    public void updateProduct(ProductEntity product) {
        if (product == null || !products.containsKey(product.getId())) {
            System.out.println("Ошибка: товар для обновления не найден.");
            return;
        }
        products.put(product.getId(), product);
        FileStorage.saveMap(file, products);
    }

    public ProductEntity getProduct(int id) {
        return products.get(id);
    }

    public Collection<ProductEntity> getAllProducts() {
        return products.values();
    }

    public List<ProductEntity> search(String keyword) {
        List<ProductEntity> res = new ArrayList<>();
        for (ProductEntity p : products.values()) {
            if (p.getName().toLowerCase().contains(keyword.toLowerCase())) {
                res.add(p);
            }
        }
        return res;
    }

}
