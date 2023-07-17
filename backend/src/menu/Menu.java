package menu;

import product.Product;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Menu {
    private static int lastId = 0;
    private int id;
    private String name;
    private Set<Product> products;
    private String menuCode;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer updatedBy;

    public Menu(String name, String menuCode, Integer createdBy) {
        this.id = ++lastId;
        this.name = name;
        this.products = new HashSet<>();
        this.menuCode = menuCode;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
    }

    public Menu(int id, String name, String menuCode, Integer updatedBy) {
        this.id = id;
        this.name = name;
        this.menuCode = menuCode;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "Menu {\n" +
                "\tid = " + id + "\n" +
                "\tname = '" + name + '\'' + "\n" +
                "\tproducts = " + products + "\n" +
                "\tmenuCode = '" + menuCode + '\'' + "\n" +
                "\tisActive = " + isActive + "\n" +
                "\tcreatedAt = " + createdAt + "\n" +
                "\tupdatedAt = " + updatedAt + "\n" +
                "\tcreatedBy = " + createdBy + "\n" +
                "\tupdatedBy = " + updatedBy + "\n" +
                '}';
    }
}
