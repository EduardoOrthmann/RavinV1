package table;

import customer.Customer;
import enums.Role;
import enums.TableStatus;
import order.Order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Table {
    private static int lastId = 0;
    private int id;
    private String name;
    private short tableNumber;
    private short maxCapacity;
    private TableStatus status;
    private Set<Customer> customers;
    private List<Order> orders;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Role createdBy;
    private Role updatedBy;

    public Table(String name, short tableNumber, short maxCapacity, Role createdBy) {
        this.id = ++lastId;
        this.name = name;
        this.tableNumber = tableNumber;
        this.maxCapacity = maxCapacity;
        this.status = TableStatus.AVAILABLE;
        this.customers = new HashSet<>();
        this.orders = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
    }

    public Table(int id, String name, short tableNumber, short maxCapacity, TableStatus status, Role updatedBy) {
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

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
