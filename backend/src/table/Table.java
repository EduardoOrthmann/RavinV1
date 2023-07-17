package table;

import customer.Customer;
import enums.TableStatus;
import order.Order;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Table {
    private static int lastId = 0;
    private int id;
    private String name;
    private short tableNumber;
    private short maxCapacity;
    private TableStatus status;
    private Set<Customer> customers;
    private Set<Order> orders;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer updatedBy;

    public Table(String name, short tableNumber, short maxCapacity, Integer createdBy) {
        this.id = ++lastId;
        this.name = name;
        this.tableNumber = tableNumber;
        this.maxCapacity = maxCapacity;
        this.status = TableStatus.AVAILABLE;
        this.customers = new HashSet<>();
        this.orders = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
    }

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

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "Table {\n" +
                "\tid = " + id + "\n" +
                "\tname = '" + name + '\'' + "\n" +
                "\ttableNumber = " + tableNumber + "\n" +
                "\tmaxCapacity = " + maxCapacity + "\n" +
                "\tstatus = " + status + "\n" +
                "\tcustomers = " + customers + "\n" +
                "\torders = " + orders + "\n" +
                "\tcreatedAt = " + createdAt + "\n" +
                "\tupdatedAt = " + updatedAt + "\n" +
                "\tcreatedBy = " + createdBy + "\n" +
                "\tupdatedBy = " + updatedBy + "\n" +
                '}';
    }
}
