package sukhov.danila.domain.entities;

import java.io.Serializable;

public class BrandEntity implements Serializable {
    private String name;
    private String ownerUsername;

    public BrandEntity(String name, String ownerUsername) {
        this.name = name;
        this.ownerUsername = ownerUsername;
    }

    public String getName() {return name;}
    public String getOwnerUsername() {return ownerUsername;}

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BrandEntity)) return false;
        return ((BrandEntity) o).name.equalsIgnoreCase(this.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}
