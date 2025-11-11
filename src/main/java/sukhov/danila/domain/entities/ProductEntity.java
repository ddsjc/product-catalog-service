package sukhov.danila.domain.entities;

import java.io.Serializable;

public class ProductEntity implements Serializable {
    private static int counter = 1;
    private int id;
    private String name;
    private CategoryEntity category;
    private BrandEntity brand;
    private double price;
    private String ownerUsername;

    public ProductEntity(String name, CategoryEntity category, BrandEntity brand, double price, String ownerUserName) {
        this.id = counter++;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.ownerUsername = ownerUserName;
    }

    public int getId() {return id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public CategoryEntity getCategory() {return category;}
    public void setCategory(CategoryEntity category) {this.category = category;}
    public BrandEntity getBrand() {return brand;}
    public void setBrand(BrandEntity brand) {this.brand = brand;}
    public double getPrice() {return price;}
    public void setPrice(double price) {this.price = price;}

    @Override
    public String toString() {
        return String.format("[%d] %s | Категория: %s | Бренд: %s | Цена: %.2f", id, name, category, brand, price);
    }
}
