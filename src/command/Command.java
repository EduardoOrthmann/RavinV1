package command;

import enums.Role;
import order.Order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Command {
    private static int lastId = 0;
    private int id;
    private boolean isPaid;
    private List<Order> orders;
    private double totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Role createdBy;
    private Role updatedBy;

    public Command(Role createdBy) {
        this.id = ++lastId;
        this.isPaid = false;
        this.orders = new ArrayList<>();
        this.totalPrice = 0;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
    }

    public Command(int id, Role updatedBy) {
        this.id = id;
        this.updatedAt = LocalDateTime.now();
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
