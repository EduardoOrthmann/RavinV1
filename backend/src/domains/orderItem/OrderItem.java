package domains.orderItem;

import domains.product.Product;
import enums.OrderItemStatus;
import interfaces.Auditable;

import java.time.LocalDateTime;
import java.util.List;

public class OrderItem implements Auditable {
    private int id;
    private int orderId;
    private Product product;
    private Integer employeeId;
    private int quantity;
    private double totalPrice;
    private OrderItemStatus status;
    private List<String> notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer updatedBy;

    public OrderItem(int id, int orderId, Product product, Integer employeeId, int quantity, double totalPrice, OrderItemStatus status, List<String> notes, LocalDateTime createdAt, LocalDateTime updatedAt, Integer createdBy, Integer updatedBy) {
        this.id = id;
        this.orderId = orderId;
        this.product = product;
        this.employeeId = employeeId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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

    public OrderItemStatus getStatus() {
        return status;
    }

    public void setStatus(OrderItemStatus status) {
        this.status = status;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public Integer getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Integer getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }
}
