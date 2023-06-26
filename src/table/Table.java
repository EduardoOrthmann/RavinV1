package table;

import customer.Customer;
import employee.Employee;
import enums.TableStatus;

import java.time.LocalDateTime;
import java.util.Set;

public class Table {
    private int id;
    private String name;
    private short tableNumber;
    private short maxCapacity;
    private TableStatus status;
    private Set<Customer> customers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Employee createdBy;
    private Employee updatedBy;

    public Table(int id, String name, short tableNumber, short maxCapacity, TableStatus status, Set<Customer> customers, LocalDateTime createdAt, LocalDateTime updatedAt, Employee createdBy, Employee updatedBy) {
        this.id = id;
        this.name = name;
        this.tableNumber = tableNumber;
        this.maxCapacity = maxCapacity;
        this.status = status;
        this.customers = customers;
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

    public Employee getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Employee createdBy) {
        this.createdBy = createdBy;
    }

    public Employee getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Employee updatedBy) {
        this.updatedBy = updatedBy;
    }
}
