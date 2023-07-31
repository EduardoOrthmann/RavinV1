package domains.reservedTableCustomer;

import domains.customer.Customer;

public class ReservedTableCustomer {
    private int id;
    private int reservedTableId;
    private Customer customer;

    public ReservedTableCustomer(int id, int reservedTableId, Customer customer) {
        this.id = id;
        this.reservedTableId = reservedTableId;
        this.customer = customer;
    }

    public ReservedTableCustomer(int reservedTableId, Customer customer) {
        this.reservedTableId = reservedTableId;
        this.customer = customer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReservedTableId() {
        return reservedTableId;
    }

    public void setReservedTableId(int reservedTableId) {
        this.reservedTableId = reservedTableId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
