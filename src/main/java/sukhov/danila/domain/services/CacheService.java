package sukhov.danila.domain.services;

import sukhov.danila.domain.entities.ProductEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheService {
    private final Map<String, List<ProductEntity>> cache = new HashMap<>();
    public void put(String key, List<ProductEntity> products){
        cache.put(key, new ArrayList<>(products));
    }
    public List<ProductEntity> get(String key){
        return cache.getOrDefault(key,new ArrayList<>());
    }
    public boolean contains(String key){
        return cache.containsKey(key);
    }
}
