package table;

import enums.TableStatus;
import interfaces.Auditable;
import order.Order;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Table implements Auditable {
    private static int lastId = 0;
    private int id;
    private String name;
    private short tableNumber;
    private short maxCapacity;
    private TableStatus status;
    private Set<Order> orders;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer updatedBy;

    // insert
    public Table(String name, short tableNumber, short maxCapacity, Integer createdBy) {
        this.id = ++lastId;
        this.name = name;
        this.tableNumber = tableNumber;
        this.maxCapacity = maxCapacity;
        this.status = TableStatus.AVAILABLE;
        this.orders = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
    }

    // update
    public Table(int id, String name, short tableNumber, short maxCapacity, TableStatus status, Integer updatedBy) {
        this.id = id;
        this.name = name;
        this.tableNumber = tableNumber;
        this.maxCapacity = maxCapacity;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(short tableNumber) {
        this.tableNumber = tableNumber;
    }

    public short getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(short maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public TableStatus getStatus() {
        return status;
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
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
