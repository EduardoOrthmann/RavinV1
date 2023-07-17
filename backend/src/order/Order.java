package order;

import enums.OrderStatus;
import product.Product;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private static int lastId = 0;
    private int id;
    private Product product;
    private int quantity;
    private double price;
    private Integer employeeId;
    private Integer customerId;
    private OrderStatus status;
    private List<String> notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer updatedBy;

    // insert
    public Order(Product product, int quantity, Integer customerId, List<String> notes) {
        this.id = ++lastId;
        this.product = product;
        this.quantity = quantity;
        this.customerId = customerId;
        this.notes = notes;
        this.status = OrderStatus.WAITING;
        this.createdAt = LocalDateTime.now();
        this.createdBy = customerId;
    }

    // update
    public Order(int id, Product product, int quantity, Integer customerId, List<String> notes) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.customerId = customerId;
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = customerId;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "Order {\n" +
                "\tid = " + id + "\n" +
                "\tproduct = " + product + "\n" +
                "\temployee = " + employeeId + "\n" +
                "\tcustomer = " + customerId + "\n" +
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
