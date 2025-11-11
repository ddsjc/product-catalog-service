package sukhov.danila.domain.entities;

import java.io.Serializable;
import java.util.Objects;

public class CategoryEntity implements Serializable {
    private String name;
    public CategoryEntity(String name) {
        this.name = name;
    }
    public String getName() {return name;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryEntity)) return false;
        CategoryEntity that = (CategoryEntity) o;
        return Objects.equals(name.toLowerCase(), that.name.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }

    @Override
    public String toString() {
        return name;
    }
}
