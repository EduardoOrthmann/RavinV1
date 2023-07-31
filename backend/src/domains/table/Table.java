package domains.table;

import domains.customer.Customer;
import enums.TableStatus;
import interfaces.Auditable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Table implements Auditable {
    private int id;
    private String name;
    private short tableNumber;
    private short maxCapacity;
    private Set<Customer> customers;
    private TableStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer updatedBy;

    public Table(int id, String name, short tableNumber, short maxCapacity, Set<Customer> customers, TableStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, Integer createdBy, Integer updatedBy) {
        this.id = id;
        this.name = name;
        this.tableNumber = tableNumber;
        this.maxCapacity = maxCapacity;
        this.customers = customers;
        this.status = status;
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

    public Set<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
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
