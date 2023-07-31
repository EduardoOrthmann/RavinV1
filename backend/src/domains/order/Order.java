package domains.order;

import domains.orderItem.OrderItem;
import domains.table.Table;
import interfaces.Auditable;

import java.time.LocalDateTime;
import java.util.Set;

public class Order implements Auditable {
    private int id;
    private int tableId;
    private int customerId;
    private boolean isPaid;
    private double totalPrice;
    private Set<OrderItem> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer updatedBy;

    public Order(int id, int tableId, int customerId, boolean isPaid, double totalPrice, Set<OrderItem> orderItems, LocalDateTime createdAt, LocalDateTime updatedAt, Integer createdBy, Integer updatedBy) {
        this.id = id;
        this.tableId = tableId;
        this.customerId = customerId;
        this.isPaid = isPaid;
        this.totalPrice = totalPrice;
        this.orderItems = orderItems;
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

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
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
