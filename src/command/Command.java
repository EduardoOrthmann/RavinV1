package command;

import customer.Customer;
import enums.Role;
import order.Order;
import table.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Command {
    private static int lastId = 0;
    private int id;
    private Table table;
    private Customer customer;
    private boolean isPaid;
    private List<Order> orders;
    private double totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Role createdBy;
    private Role updatedBy;

    public Command(Table table, Customer customer, Role createdBy) {
        this.id = ++lastId;
        this.table = table;
        this.customer = customer;
        this.isPaid = false;
        this.orders = new ArrayList<>();
        this.totalPrice = 0;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
    }

    public Command(int id, Table table, Customer customer, Role updatedBy) {
        this.id = id;
        this.table = table;
        this.customer = customer;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
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
        return "Command {\n" +
                "\tid = " + id + "\n" +
                "\ttable = " + table + "\n" +
                "\tcustomer = " + customer + "\n" +
                "\tisPaid = " + isPaid + "\n" +
                "\torders = " + orders + "\n" +
                "\ttotalPrice = " + totalPrice + "\n" +
                "\tcreatedAt = " + createdAt + "\n" +
                "\tupdatedAt = " + updatedAt + "\n" +
                "\tcreatedBy = " + createdBy + "\n" +
                "\tupdatedBy = " + updatedBy + "\n" +
                '}';
    }
}
