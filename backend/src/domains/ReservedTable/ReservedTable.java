package domains.ReservedTable;

import domains.customer.Customer;
import domains.table.Table;
import interfaces.Auditable;

import java.time.LocalDateTime;
import java.util.Set;

public class ReservedTable implements Auditable {
    private static int lastId = 0;
    private int id;
    private Set<Customer> customers;
    private Table table;
    private int numberOfPeople;
    private Reservation reservedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer updatedBy;

    // insert
    public ReservedTable(Set<Customer> customers, Reservation reservedAt, Table table, int numberOfPeople, Integer createdBy) {
        this.id = ++lastId;
        this.customers = customers;
        this.reservedAt = reservedAt;
        this.table = table;
        this.numberOfPeople = numberOfPeople;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public Reservation getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(Reservation reservedAt) {
        this.reservedAt = reservedAt;
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
