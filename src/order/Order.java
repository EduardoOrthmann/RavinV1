package order;

import employee.Employee;
import enums.OrderStatus;
import enums.Role;
import product.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private static int lastId = 0;
    private int id;
    private Product product;
    private Employee employee;
    private int quantity;
    private double price;
    private OrderStatus status;
    private List<String> notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Role createdBy;
    private Role updatedBy;

    public Order(Product product, Employee employee, int quantity, OrderStatus status, Role createdBy) {
        this.id = ++lastId;
        this.product = product;
        this.employee = employee;
        this.quantity = quantity;
        this.status = status;
        this.price = product.getSalePrice() * quantity;
        this.notes = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
    }

    public Order(int id, Product product, Employee employee, int quantity, OrderStatus status, Role updatedBy) {
        this.id = id;
        this.product = product;
        this.employee = employee;
        this.quantity = quantity;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
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

    public Role getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Role createdBy) {
        this.createdBy = createdBy;
    }

    public Role getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Role updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Order {\n" +
                "\tid = " + id + "\n" +
                "\tproduct = " + product + "\n" +
                "\temployee = " + employee + "\n" +
                "\tquantity = " + quantity + "\n" +
                "\tprice = " + price + "\n" +
                "\tstatus = " + status + "\n" +
                "\tnotes = " + notes + "\n" +
                "\tcreatedAt = " + createdAt + "\n" +
                "\tupdatedAt = " + updatedAt + "\n" +
                "\tcreatedBy = " + createdBy + "\n" +
                "\tupdatedBy = " + updatedBy + "\n" +
                '}';
    }
}
